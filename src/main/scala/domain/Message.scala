package es.eriktorr.coffee_machine
package domain

import domain.CoffeeMachineError.{CoffeeMachineValidation, InvalidMessageDelivery}

import cats.syntax.all.*
import squants.market.Money

opaque type Message = String

object Message:
  def unsafeFrom(value: String): Message = value

  def from(value: String): CoffeeMachineValidation[Message] = if value.nonEmpty then
    unsafeFrom(value).validNec
  else InvalidMessageDelivery("Message cannot be empty").invalidNec

  def shortage(drink: Drink): Message =
    s"There is not enough ${drink.name} to process your order. A notification has been sent"

  def underpayment(amount: Money): Message = s"Insufficient money: $amount missing"

  extension (message: Message) def value: String = message
