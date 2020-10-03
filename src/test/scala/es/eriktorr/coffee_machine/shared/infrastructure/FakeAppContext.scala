package es.eriktorr.coffee_machine.shared.infrastructure

import cats.effect._
import cats.effect.concurrent.Ref
import es.eriktorr.coffee_machine.{AppContext, Drink, DrinkMaker, Sale, Sales, Statement}

trait FakeAppContext {
  val appContext: AppContext = AppContext()

  def testScenario
    : (DrinkMaker[IO], FakeEmailNotifier, Sales[IO], Ref[IO, List[Sale]], FakeStatementsPrinter) =
    testScenario(List.empty)

  def testScenario(
    missingDrinks: List[Drink]
  ): (DrinkMaker[IO], FakeEmailNotifier, Sales[IO], Ref[IO, List[Sale]], FakeStatementsPrinter) = {
    val statementsPrinter = new FakeStatementsPrinter(Ref.unsafe[IO, List[Statement]](List.empty))
    val salesRef = Ref.unsafe[IO, List[Sale]](List.empty)
    val sales = Sales.impl[IO](salesRef, statementsPrinter)
    val emailNotifier = new FakeEmailNotifier(Ref.unsafe[IO, List[Drink]](List.empty))
    val drinkMaker = DrinkMaker.impl[IO](
      appContext,
      new FakeBeverageQuantityChecker(missingDrinks),
      emailNotifier,
      sales
    )
    (drinkMaker, emailNotifier, sales, salesRef, statementsPrinter)
  }
}
