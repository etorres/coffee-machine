package es.eriktorr.coffee_machine

trait BeverageQuantityChecker[F[_]] {
  def isEmpty(drink: Drink): F[Boolean]
}
