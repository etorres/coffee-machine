package es.eriktorr.coffee_machine
package infrastructure

import domain.{BeverageQuantityChecker, Drink}

import cats.effect.{IO, Ref}

final case class BeverageAvailability(availableDrinks: List[Drink]):
  def setAvailableDrinks(newAvailableDrinks: List[Drink]): BeverageAvailability = copy(
    newAvailableDrinks,
  )

object BeverageAvailability:
  val empty: BeverageAvailability = BeverageAvailability(List.empty)

final class FakeBeverageQuantityChecker(stateRef: Ref[IO, BeverageAvailability])
    extends BeverageQuantityChecker:
  override def nonEmpty(drink: Drink): IO[Boolean] =
    stateRef.get.map(_.availableDrinks.contains(drink))
