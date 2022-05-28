package es.eriktorr.coffee_machine
package domain

import domain.CoffeeMachineError.{CoffeeMachineValidation, InvalidCustomerOrder}

import cats.Show
import cats.syntax.all.*

enum Sugar(val amount: Int):
  case Zero extends Sugar(0)
  case One extends Sugar(1)
  case Two extends Sugar(2)

object Sugar:
  def from(amount: Int): CoffeeMachineValidation[Sugar] =
    values.find(_.amount == amount) match
      case None => InvalidCustomerOrder(s"Unsupported sugar amount: $amount").invalidNec
      case Some(sugar) => sugar.validNec

  given Show[Sugar] = Show.show(sugar =>
    sugar match
      case Zero => ""
      case nonZero => nonZero.amount.toString,
  )
