package es.eriktorr.coffee_machine
package domain

import domain.CoffeeMachineError.{CoffeeMachineValidation, InvalidCustomerOrder}

import cats.Show
import cats.syntax.all.*

enum Drink(val code: String, val name: String):
  case Chocolate extends Drink("H", "Chocolate")
  case Coffee extends Drink("C", "Coffee")
  case OrangeJuice extends Drink("O", "Orange Juice")
  case Tea extends Drink("T", "Tea")

object Drink:
  def from(code: String): CoffeeMachineValidation[Drink] =
    values.find(_.code == code) match
      case None => InvalidCustomerOrder(s"Unsupported drink code: $code").invalidNec
      case Some(drink) => drink.validNec

  def allDrinks: List[Drink] = values.toList

  extension (drink: Drink) def canBeExtraHot: Boolean = drink != OrangeJuice

  given Show[Drink] = Show.show(_.code)
