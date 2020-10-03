package es.eriktorr.coffee_machine.shared.infrastructure

import cats.effect._
import es.eriktorr.coffee_machine.{BeverageQuantityChecker, Drink}

final class FakeBeverageQuantityChecker(missingDrinks: List[Drink])
    extends BeverageQuantityChecker[IO] {
  override def isEmpty(drink: Drink): IO[Boolean] = IO(missingDrinks.contains(drink))
}
