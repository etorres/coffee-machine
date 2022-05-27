package es.eriktorr.coffee_machine
package domain

import cats.Show

opaque type ExtraHot = Boolean

object ExtraHot:
  def apply(value: Boolean): ExtraHot = value

  extension (extraHot: ExtraHot) def value: Boolean = extraHot

  given Show[ExtraHot] = Show.show(extraHot => if extraHot.value then "h" else "")
