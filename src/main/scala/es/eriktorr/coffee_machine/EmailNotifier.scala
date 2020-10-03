package es.eriktorr.coffee_machine

trait EmailNotifier[F[_]] {
  def notifyMissingDrink(drink: Drink): F[Unit]
}
