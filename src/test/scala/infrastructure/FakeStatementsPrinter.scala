package es.eriktorr.coffee_machine
package infrastructure

import domain.{Statement, StatementsPrinter}

import cats.effect.{IO, Ref}

final case class StatementsPrinted(statements: List[Statement]):
  def setStatements(newStatements: List[Statement]): StatementsPrinted = copy(newStatements)

object StatementsPrinted:
  val empty: StatementsPrinted = StatementsPrinted(List.empty)

final class FakeStatementsPrinter(stateRef: Ref[IO, StatementsPrinted]) extends StatementsPrinter:
  override def print(statement: Statement): IO[Unit] =
    stateRef.update(currentState => currentState.copy(statement :: currentState.statements))
