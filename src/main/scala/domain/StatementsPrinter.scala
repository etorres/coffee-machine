package es.eriktorr.coffee_machine
package domain

import cats.effect.IO

trait StatementsPrinter:
  def print(statement: Statement): IO[Unit]
