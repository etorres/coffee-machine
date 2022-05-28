package es.eriktorr.coffee_machine
package domain

import cats.data.ValidatedNec

import scala.util.control.NoStackTrace

@SuppressWarnings(Array("org.wartremover.warts.Null"))
sealed abstract class CoffeeMachineError(
    message: String,
    cause: Option[Throwable] = Option.empty[Throwable],
) extends NoStackTrace:
  import scala.language.unsafeNulls
  override def getCause: Throwable = cause.orNull
  override def getMessage: String = message

object CoffeeMachineError:
  final case class InvalidCustomerOrder(message: String) extends CoffeeMachineError(message)
  final case class InvalidMessageDelivery(message: String) extends CoffeeMachineError(message)

  type CoffeeMachineValidation[A] = ValidatedNec[CoffeeMachineError, A]
