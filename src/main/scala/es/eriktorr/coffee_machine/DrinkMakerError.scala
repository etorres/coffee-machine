package es.eriktorr.coffee_machine

import scala.util.control.NoStackTrace

sealed trait DrinkMakerError extends NoStackTrace with Product with Serializable

object DrinkMakerError {
  final case class InvalidCommand(errorMessage: String) extends DrinkMakerError
}
