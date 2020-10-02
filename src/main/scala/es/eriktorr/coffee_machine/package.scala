package es.eriktorr

import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import io.estatico.newtype.macros.newtype
import squants.market.Money

package object coffee_machine extends ResourceSyntax {
  type ZeroToTwo = Int Refined Interval.Closed[0, 2]

  @newtype case class Command(toText: String Refined NonEmpty)

  @newtype case class DrinkPrices(toMap: Map[Drink, Money])

  @newtype case class ExtraHot(toBoolean: Boolean)

  @newtype case class Stick(toBoolean: Boolean)

  @newtype case class Sugar(toInt: ZeroToTwo)
}
