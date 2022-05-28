package es.eriktorr.coffee_machine
package domain

import cats.Show

opaque type Stick = Boolean

object Stick:
  def apply(value: Boolean): Stick = value

  extension (stick: Stick) def value: Boolean = stick

  given Show[Stick] = Show.show(stick => if stick.value then "0" else "")
