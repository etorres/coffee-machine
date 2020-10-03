package es.eriktorr.coffee_machine

import es.eriktorr.coffee_machine.shared.infrastructure.FakeAppContext
import eu.timepit.refined.auto._
import squants.market.MoneyConversions._
import weaver._

object ReportPrinterSuite extends SimpleIOSuite with FakeAppContext {
  simpleTest("Save sales") {
    val (drinkMaker, _, _, salesRef, _) = testScenario
    val (sale1, sale2) = (Sale(Coffee, 0.6.EUR), Sale(Chocolate, 0.5.EUR))
    for {
      _ <- drinkMaker.make(sale1.profit, Command("C::"))
      _ <- drinkMaker.make(sale2.profit, Command("H::"))
      currentSales <- salesRef.get
    } yield expect(currentSales == List(sale2, sale1))
  }

  simpleTest("Print how many of each drink was sold and the total amount of money earned") {
    val (drinkMaker, _, sales, _, statementsPrinter) = testScenario
    for {
      _ <- drinkMaker.make(0.5.EUR, Command("H::"))
      _ <- drinkMaker.make(0.4.EUR, Command("T::"))
      _ <- drinkMaker.make(0.4.EUR, Command("T::"))
      _ <- drinkMaker.make(0.6.EUR, Command("C::"))
      _ <- drinkMaker.make(0.5.EUR, Command("H::"))
      _ <- drinkMaker.make(0.4.EUR, Command("T::"))
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
