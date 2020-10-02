package es.eriktorr.coffee_machine

import cats.effect._
import cats.effect.concurrent.Ref
import es.eriktorr.coffee_machine.shared.infrastructure.FakeStatementsPrinter
import squants.market.MoneyConversions._
import weaver._

object SalesSuite extends SimpleIOSuite {
  simpleTest("Save sales") {
    val ref = Ref.unsafe[IO, List[Sale]](List.empty)
    val statementsPrinter = new FakeStatementsPrinter(Ref.unsafe[IO, List[Statement]](List.empty))
    val sales = Sales.impl[IO](ref, statementsPrinter)

    val (sale1, sale2) = (Sale(Coffee, 0.6.EUR), Sale(Chocolate, 0.5.EUR))
    for {
      _ <- sales.save(sale1)
      _ <- sales.save(sale2)
      currentSales <- ref.get
    } yield expect(currentSales == List(sale2, sale1))
  }

  simpleTest("Print how many of each drink was sold and the total amount of money earned") {
    val ref = Ref.unsafe[IO, List[Sale]](List.empty)
    val statementsPrinter = new FakeStatementsPrinter(Ref.unsafe[IO, List[Statement]](List.empty))
    val sales = Sales.impl[IO](ref, statementsPrinter)
    for {
      _ <- sales.save(Sale(Chocolate, 0.5.EUR))
      _ <- sales.save(Sale(Tea, 0.4.EUR))
      _ <- sales.save(Sale(Tea, 0.4.EUR))
      _ <- sales.save(Sale(Coffee, 0.6.EUR))
      _ <- sales.save(Sale(Chocolate, 0.5.EUR))
      _ <- sales.save(Sale(Tea, 0.4.EUR))
      _ <- sales.printReport
      printedStatements <- statementsPrinter.ref.get
    } yield expect(
      printedStatements == List(
        Statement("Chocolate || 1 EUR"),
        Statement("Tea || 1.2 EUR"),
        Statement("Coffee || 0.6 EUR"),
        Statement("Drink || Total revenue")
      )
    )
  }
}
