package es.eriktorr.coffee_machine

trait StatementsPrinter[F[_]] {
  def print(statement: Statement): F[Unit]
}
