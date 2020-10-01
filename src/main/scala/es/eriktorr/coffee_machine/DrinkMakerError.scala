package es.eriktorr.coffee_machine

import scala.util.control.NoStackTrace

sealed trait DrinkMakerError extends NoStackTrace

final case class InvalidCommand(errorMessage: String) extends DrinkMakerError
