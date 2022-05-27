package es.eriktorr.coffee_machine
package domain

import CoffeeMachineError.{CoffeeMachineValidation, InvalidCustomerOrder}
import cats.Show
import cats.syntax.all.*

sealed trait Command

object Command:
  final case class CustomerOrder(drink: Drink, extraHot: ExtraHot, sugar: Sugar, stick: Stick)
      extends Command

  final case class MessageDelivery(message: Message) extends Command

  given Show[CustomerOrder] = Show.show { x =>
    val drink = s"${x.drink.show}${x.extraHot.show}"
    List(drink, x.sugar.show, x.stick.show).mkString(":")
  }

  given Show[MessageDelivery] = Show.show(messageDelivery => s"M:${messageDelivery.message}")

  given Show[Command] = cats.derived.semiauto.show

  def from(text: String): CoffeeMachineValidation[Command] =
    val customerOrderPattern = raw"(?<drink>[CHOT])(?<extraHot>h)?:(?<sugar>[0-2])?:(?<stick>0)?".r
    val messageDeliveryPattern = raw"M:(?<message>[\w\s\-]+)".r
    text match
      case customerOrderPattern(drinkCode, extraHotOption, amountOfSugar, hasStick) =>
        (
          Drink.from(drinkCode),
          ExtraHot(orNormal(extraHotOption)).validNec[CoffeeMachineError],
          Sugar.from(orNone(amountOfSugar)),
          Stick(orFalse(hasStick)).validNec[CoffeeMachineError],
        ).mapN(CustomerOrder.apply).andThen { customerOrder =>
          if !customerOrder.extraHot.value || customerOrder.drink.canBeExtraHot then
            customerOrder.validNec[CoffeeMachineError]
          else
            InvalidCustomerOrder(s"Cannot make extra hot ${customerOrder.drink.name}")
              .invalidNec[Command]
        }
      case messageDeliveryPattern(message) => Message.from(message).map(MessageDelivery.apply)
      case _ => InvalidCustomerOrder("Invalid format").invalidNec[Command]

  private[this] def orNone(value: String) = Option(value).map(_.toInt).getOrElse(0)

  private[this] def orNormal(value: String) = Option(value).contains("h")

  private[this] def orFalse(value: String) = Option(value).exists(_.toInt == 0)
