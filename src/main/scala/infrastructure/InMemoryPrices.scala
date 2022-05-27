package es.eriktorr.coffee_machine
package infrastructure

import domain.{Drink, Prices}

import cats.effect.IO
import squants.Money
import squants.market.MoneyConversions.*

final class InMemoryPrices extends Prices:
  override def howMuchForA(drink: Drink): IO[Money] = IO.pure(drink match
    case Drink.Chocolate => 0.5.EUR
    case Drink.Coffee => 0.6.EUR
    case Drink.OrangeJuice => 0.6.EUR
    case Drink.Tea => 0.4.EUR,
  )
