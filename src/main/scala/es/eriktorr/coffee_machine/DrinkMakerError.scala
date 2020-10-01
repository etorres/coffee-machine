package es.eriktorr.coffee_machine

import scala.util.control.NoStackTrace

sealed trait DrinkMakerError extends NoStackTrace

case object InvalidCommand extends DrinkMakerError
