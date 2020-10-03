package es.eriktorr.coffee_machine

import cats._
import cats.implicits._
import eu.timepit.refined.api.Refined.unsafeApply
import squants.market.Money
import squants.market.MoneyConversions._

trait DrinkMaker[F[_]] {
  def make(payment: Money, command: Command): F[DrinkMakerOrder]
}

object DrinkMaker {
  implicit def apply[F[_]](implicit ev: DrinkMaker[F]): DrinkMaker[F] = ev

  type Tokens = (Char, Boolean, Int, Boolean, Option[String])

  private[this] def drinkTokensFrom(token: String) = token.toCharArray.toList match {
    case x :: 'h' :: Nil => (x, true).some
    case x :: Nil => (x, false).some
    case _ => None
  }

  private[this] def maybeTokens[F[_]: Monad](
    command: Command
  ) = Monad[F].pure(
    command.toText.value.split(":").toList match {
      case x :: y :: _ :: Nil =>
        drinkTokensFrom(x).fold(none[Tokens]) {
          case (drink, extraHot) =>
            val sugar = y.toInt
            (drink, extraHot, sugar, if (sugar > 0) true else false, none[String]).some
        }
      case x :: Nil =>
        drinkTokensFrom(x).fold(none[Tokens]) {
          case (drink, extraHot) => (drink, extraHot, 0, false, none[String]).some
        }
      case "M" :: y :: Nil => ('M', false, 0, false, y.some).some
      case _ => none[Tokens]
    }
  )

  private[this] def orderFrom[F[_]: MonadError[*[_], Throwable]](tokens: Tokens) = tokens match {
    case ('M', _, _, _, messageContent) => messageOrder[F](messageContent)
    case (drink, extraHot, sugar, stick, _) => drinkOrder[F](drink, extraHot, sugar, stick)
  }

  private[this] def messageOrder[F[_]: MonadError[*[_], Throwable]](
    content: Option[String]
  ) =
    content.fold(InvalidCommand("Message content expected").raiseError[F, DrinkMakerOrder])(c =>
      Monad[F].pure(CoffeeMachineMessage(c))
    )

  private[this] def drinkOrder[F[_]: MonadError[*[_], Throwable]](
    drink: Char,
    extraHot: Boolean,
    sugar: Int,
    stick: Boolean
  ) =
    Drink
      .fromCode(drink)
      .fold(InvalidCommand(s"Unknown drink ${drink.toString}").raiseError[F, DrinkMakerOrder])(d =>
        Monad[F].pure(DrinkOrder(d, Sugar(unsafeApply(sugar)), Stick(stick), ExtraHot(extraHot)))
      )

  def impl[F[_]: MonadError[*[_], Throwable]](
    appContext: AppContext,
    sales: Sales[F]
  ): DrinkMaker[F] =
    (payment: Money, command: Command) => {
      for {
        tokens <- maybeTokens[F](command)
        order <- tokens.fold(
          InvalidCommand(s"Invalid command received ${command.toText.value}")
            .raiseError[F, DrinkMakerOrder]
        )(orderFrom[F])
        (paidOrder, sale) = order match {
          case drinkOrder: DrinkOrder =>
            val price = appContext.priceOf(drinkOrder.drink)
            val priceDiff = price - payment
            if (priceDiff <= 0.EUR) (drinkOrder, Sale(drinkOrder.drink, price).some)
            else
              (CoffeeMachineMessage(s"Not enough money, missing ${priceDiff.toString}"), none[Sale])
          case message: CoffeeMachineMessage => (message, none[Sale])
        }
        _ <- sale.fold(Monad[F].unit)(sales.save)
      } yield paidOrder
    }
}
