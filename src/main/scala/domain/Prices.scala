package es.eriktorr.coffee_machine
package domain

import cats.effect.IO
import squants.Money

trait Prices:
  def howMuchForA(drink: Drink): IO[Money]
