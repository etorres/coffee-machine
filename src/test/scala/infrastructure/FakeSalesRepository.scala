package es.eriktorr.coffee_machine
package infrastructure

import domain.{Sale, SalesRepository}

import cats.effect.{IO, Ref}

final case class DrinksSold(sales: List[Sale]):
  def setSales(newSales: List[Sale]): DrinksSold = copy(newSales)

object DrinksSold:
  val empty: DrinksSold = DrinksSold(List.empty)

final class FakeSalesRepository(stateRef: Ref[IO, DrinksSold]) extends SalesRepository:
  override def save(sale: Sale): IO[Unit] =
    stateRef.update(currentState => currentState.copy(sale :: currentState.sales))

  override def allSales: IO[List[Sale]] = stateRef.get.map(_.sales)
