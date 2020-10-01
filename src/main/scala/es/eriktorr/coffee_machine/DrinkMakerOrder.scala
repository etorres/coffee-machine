package es.eriktorr.coffee_machine

sealed trait DrinkMakerOrder

final case class DrinkOrder(drink: Drink, sugar: Sugar, stick: Stick) extends DrinkMakerOrder
final case class Message(content: String) extends DrinkMakerOrder
