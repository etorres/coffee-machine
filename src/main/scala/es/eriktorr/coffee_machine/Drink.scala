package es.eriktorr.coffee_machine

import cats.implicits._

sealed trait Drink

case object Chocolate extends Drink
case object Coffee extends Drink
case object OrangeJuice extends Drink
case object Tea extends Drink

object Drink {
  def fromString(code: String): Option[Drink] = code match {
    case "H" => Chocolate.some
    case "C" => Coffee.some
    case "O" => OrangeJuice.some
    case "T" => Tea.some
    case _ => None
  }
}
