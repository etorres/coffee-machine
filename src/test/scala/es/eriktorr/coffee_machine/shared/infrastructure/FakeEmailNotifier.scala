package es.eriktorr.coffee_machine.shared.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import es.eriktorr.coffee_machine.{Drink, EmailNotifier}

final class FakeEmailNotifier(val ref: Ref[IO, List[Drink]]) extends EmailNotifier[IO] {
  override def notifyMissingDrink(drink: Drink): IO[Unit] =
    ref.get.flatMap(current => ref.set(drink :: current))
}
