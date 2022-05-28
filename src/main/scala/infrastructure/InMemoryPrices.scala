package es.eriktorr.coffee_machine
package infrastructure

import domain.Drink.{Chocolate, Coffee, OrangeJuice, Tea}
import domain.{Drink, Prices}

import cats.effect.IO
import squants.Money
import squants.market.MoneyConversions.*

object InMemoryPrices extends Prices:
  private[this] val fixedPrices: Map[Drink, Money] = Map(
    Chocolate -> 0.5.EUR,
    Coffee -> 0.6.EUR,
    OrangeJuice -> 0.6.EUR,
    Tea -> 0.4.EUR,
  )

  def unsafeHowMuchForA(drink: Drink): Money = fixedPrices.getOrElse(drink, fixedPrices.values.max)

  override def howMuchForA(drink: Drink): IO[Money] = IO.pure(unsafeHowMuchForA(drink))
