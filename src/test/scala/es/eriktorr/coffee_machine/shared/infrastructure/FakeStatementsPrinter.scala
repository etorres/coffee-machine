package es.eriktorr.coffee_machine.shared.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import es.eriktorr.coffee_machine.{Statement, StatementsPrinter}

final class FakeStatementsPrinter(val ref: Ref[IO, List[Statement]]) extends StatementsPrinter[IO] {
  override def print(statement: Statement): IO[Unit] =
    ref.get.flatMap(current => ref.set(statement :: current))
}
