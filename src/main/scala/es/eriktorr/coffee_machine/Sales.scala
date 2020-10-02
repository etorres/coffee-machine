package es.eriktorr.coffee_machine

import cats.effect._
import cats.effect.concurrent.Ref
import cats.syntax.all._
import squants.market.Money
import squants.market.MoneyConversions._

final case class Sale(drink: Drink, profit: Money)

trait Sales[F[_]] {
  def save(sale: Sale): F[Unit]
  def printReport: F[Unit]
}

object Sales {
  implicit def apply[F[_]](implicit ev: Sales[F]): Sales[F] = ev

  def impl[F[_]: Sync](ref: Ref[F, List[Sale]], statementsPrinter: StatementsPrinter[F]): Sales[F] =
    new Sales[F] {

      override def save(sale: Sale): F[Unit] = ref.get.flatMap(current => ref.set(sale :: current))

      override def printReport: F[Unit] =
        for {
          _ <- statementsPrinter.print(Statement("Drink || Total revenue"))
          currentSales <- ref.get
          statements = currentSales
            .groupBy(_.drink)
            .map {
              case (drink, sales) =>
                Statement(
                  s"${drink.toString} || ${sales.map(_.profit).fold(0.EUR)(_ + _).toString}"
                )
            }
            .toList
          _ <- statements.traverse_(statementsPrinter.print)
        } yield ()
    }
}
