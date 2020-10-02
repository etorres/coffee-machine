package es.eriktorr.coffee_machine

import squants.market.Money
import squants.market.MoneyConversions._

final case class AppContext(drinkPrices: DrinkPrices, priceForEveryThingElse: Money) {
  def priceOf(drink: Drink): Money =
    drinkPrices.toMap.get(drink).fold(priceForEveryThingElse)(identity)
}

object AppContext {
  def apply(): AppContext =
    new AppContext(
      DrinkPrices(
        Map(Chocolate -> 0.5.EUR, Coffee -> 0.6.EUR, OrangeJuice -> 0.6.EUR, Tea -> 0.4.EUR)
      ),
      1.0.EUR
    )

  def apply(drinkPrices: DrinkPrices, priceForEveryThingElse: Money): AppContext =
    new AppContext(drinkPrices, priceForEveryThingElse)
}
