package es.eriktorr.coffee_machine

sealed trait DrinkMakerOrder extends Product with Serializable

final case class DrinkOrder(drink: Drink, sugar: Sugar, stick: Stick, extraHot: ExtraHot)
    extends DrinkMakerOrder
final case class CoffeeMachineMessage(content: String) extends DrinkMakerOrder
