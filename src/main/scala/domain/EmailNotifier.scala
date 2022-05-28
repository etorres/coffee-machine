package es.eriktorr.coffee_machine
package domain

import cats.effect.IO

trait EmailNotifier:
  def notifyMissing(drink: Drink): IO[Unit]
