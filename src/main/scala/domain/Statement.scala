package es.eriktorr.coffee_machine
package domain

opaque type Statement = String

object Statement:
  def apply(value: String): Statement = value

  extension (statement: Statement) def value: String = statement
