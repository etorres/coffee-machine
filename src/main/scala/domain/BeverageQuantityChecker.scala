package es.eriktorr.coffee_machine
package domain

import cats.effect.IO

trait BeverageQuantityChecker:
  def nonEmpty(drink: Drink): IO[Boolean]
