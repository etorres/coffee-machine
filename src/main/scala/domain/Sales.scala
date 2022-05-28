package es.eriktorr.coffee_machine
package domain

import cats.implicits.*
import cats.effect.IO
import squants.market.Money
import squants.market.MoneyConversions.*

final case class Sale(drink: Drink, earned: Money)

trait Sales:
  def save(sale: Sale): IO[Unit]
  def printReport: IO[Unit]

trait SalesRepository:
  def save(sale: Sale): IO[Unit]
  def allSales: IO[List[Sale]]

object Sales:
  val reportHeader: Statement = Statement("Drink || Total amount of money earned")

  def impl(salesRepository: SalesRepository, statementsPrinter: StatementsPrinter): Sales =
    new Sales():
      override def save(sale: Sale): IO[Unit] = salesRepository.save(sale)

      override def printReport: IO[Unit] = for
        _ <- statementsPrinter.print(reportHeader)
        sales <- salesRepository.allSales
        _ <- statementsFrom(sales).traverse_(statementsPrinter.print)
      yield ()

  def statementsFrom(sales: List[Sale]): List[Statement] = sales
    .groupBy(_.drink)
    .map { case (drink, sales) =>
      Statement(
        s"${drink.name} || ${sales.map(_.earned).fold(0.EUR)(_ + _)}",
      )
    }
    .toList
