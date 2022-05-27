package es.eriktorr.coffee_machine
package domain

import Command.{CustomerOrder, MessageDelivery}

import cats.effect.IO
import squants.market.Money

trait DrinkMaker:
  def make(command: Command, payment: Money): IO[Unit]

object DrinkMaker:
  def impl(
      beverageQuantityChecker: BeverageQuantityChecker,
      emailNotifier: EmailNotifier,
      messageDisplay: MessageDisplay,
      prices: Prices,
      sales: Sales,
  ): DrinkMaker =
    (command: Command, payment: Money) =>
      command match
        case CustomerOrder(drink, _, _, _) =>
          for
            price <- prices.howMuchForA(drink)
            _ <-
              if payment >= price then
                beverageQuantityChecker
                  .nonEmpty(drink)
                  .ifM(
                    ifTrue = sales.save(Sale(drink, price)),
                    ifFalse = emailNotifier.notifyMissing(drink) >> messageDisplay
                      .show(Message.shortage(drink)),
                  )
              else messageDisplay.show(Message.underpayment(price - payment))
          yield ()
        case MessageDelivery(message) => messageDisplay.show(message)
