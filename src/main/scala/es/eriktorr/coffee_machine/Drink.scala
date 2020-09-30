package es.eriktorr.coffee_machine

sealed trait Drink

case object Chocolate extends Drink
case object Coffee extends Drink
case object Tea extends Drink
