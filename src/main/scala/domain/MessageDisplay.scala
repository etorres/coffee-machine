package es.eriktorr.coffee_machine
package domain

import cats.effect.IO

trait MessageDisplay:
  def show(message: Message): IO[Unit]
