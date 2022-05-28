package es.eriktorr.coffee_machine
package infrastructure

import domain.{Drink, EmailNotifier}

import cats.effect.{IO, Ref}

final case class MissingDrinkNotificationsSent(missingDrinks: List[Drink]):
  def setMissingDrinks(newMissingDrinks: List[Drink]): MissingDrinkNotificationsSent = copy(
    newMissingDrinks,
  )

object MissingDrinkNotificationsSent:
  val empty: MissingDrinkNotificationsSent = MissingDrinkNotificationsSent(List.empty)

final class FakeEmailNotifier(stateRef: Ref[IO, MissingDrinkNotificationsSent])
    extends EmailNotifier:
  override def notifyMissing(drink: Drink): IO[Unit] =
    stateRef.update(currentState => currentState.copy(drink :: currentState.missingDrinks))
