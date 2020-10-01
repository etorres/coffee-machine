package es.eriktorr.coffee_machine

import cats._
import cats.implicits._
import eu.timepit.refined.api.Refined.unsafeApply

trait OrderReceiver[F[_]] {
  def orderFrom(command: Command): F[DrinkMakerOrder]
}

object OrderReceiver {
  implicit def apply[F[_]](implicit ev: OrderReceiver[F]): OrderReceiver[F] = ev

  private[this] def maybeTokens[F[_]: Monad](
    command: Command
  ) = Monad[F].pure(
    command.toText.value.split(":").toList match {
      case x :: y :: _ :: Nil =>
        val sugar = y.toInt
        (x, sugar, if (sugar > 0) true else false, None).some
      case x :: Nil => (x, 0, false, None).some
      case "M" :: y :: Nil => ("M", 0, false, y.some).some
      case _ => None
    }
  )

  private[this] def orderFrom[F[_]: MonadError[*[_], Throwable]](
    tokens: (String, Int, Boolean, Option[String])
  ) = tokens match {
    case ("M", _, _, messageContent) => messageOrder[F](messageContent)
    case (order, sugar, stick, _) => drinkOrder[F](order, sugar, stick)
  }

  private[this] def messageOrder[F[_]: MonadError[*[_], Throwable]](
    content: Option[String]
  ) =
    content.fold(InvalidCommand("Message content expected").raiseError[F, DrinkMakerOrder])(c =>
      Monad[F].pure(CoffeeMachineMessage(c))
    )

  private[this] def drinkOrder[F[_]: MonadError[*[_], Throwable]](
    drink: String,
    sugar: Int,
    stick: Boolean
  ) =
    Drink
      .fromString(drink)
      .fold(InvalidCommand(s"Unknown drink $drink").raiseError[F, DrinkMakerOrder])(d =>
        Monad[F].pure(DrinkOrder(d, Sugar(unsafeApply(sugar)), Stick(stick)))
      )

  def impl[F[_]: MonadError[*[_], Throwable]]: OrderReceiver[F] = (command: Command) => {
    for {
      tokens <- maybeTokens[F](command)
      order <- tokens.fold(
        InvalidCommand(s"Invalid command received ${command.toText.value}")
          .raiseError[F, DrinkMakerOrder]
      )(orderFrom[F])
    } yield order
  }
}
