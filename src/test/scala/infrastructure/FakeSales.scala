package es.eriktorr.coffee_machine
package infrastructure

import domain.{Sale, Sales}

import cats.effect.{IO, Ref}

final case class DrinksSold(sales: List[Sale]):
  def setSales(newSales: List[Sale]): DrinksSold = copy(newSales)

object DrinksSold:
  val empty: DrinksSold = DrinksSold(List.empty)

final class FakeSales(stateRef: Ref[IO, DrinksSold]) extends Sales:
  override def save(sale: Sale): IO[Unit] =
    stateRef.update(currentState => currentState.copy(sale :: currentState.sales))

  override def printReport: IO[Unit] = IO.unit
