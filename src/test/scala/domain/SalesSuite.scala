package es.eriktorr.coffee_machine
package domain

import domain.Sales.{reportHeader, statementsFrom}
import domain.SalesSuite.{printReportWith, testCaseGen}
import infrastructure.*
import infrastructure.CoffeeMachineGenerators.drinkGen

import cats.effect.IO
import cats.effect.kernel.Ref
import cats.implicits.*
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.Gen
import org.scalacheck.effect.PropF.forAllF

final class SalesSuite extends CatsEffectSuite with ScalaCheckEffectSuite:

  test("it should print how many of each drink was sold and the total amount of money earned") {
    forAllF(testCaseGen)(testCase =>
      printReportWith(testCase.initialState)(testCase.sales).map { case (result, finalState) =>
        assert(result.isRight)
        assertEquals(finalState, testCase.expectedFinalState)
      },
    )
  }

object SalesSuite:
  final private case class SalesState(drinksSold: DrinksSold, statementsPrinted: StatementsPrinted)

  private object SalesState:
    val empty: SalesState = SalesState(DrinksSold.empty, StatementsPrinted.empty)

  private def printReportWith(initialState: SalesState)(sales: List[Sale]) = for
    drinksSoldRef <- Ref.of[IO, DrinksSold](initialState.drinksSold)
    statementsPrintedRef <- Ref.of[IO, StatementsPrinted](initialState.statementsPrinted)
    salesImpl = Sales.impl(
      FakeSalesRepository(drinksSoldRef),
      FakeStatementsPrinter(statementsPrintedRef),
    )
    result <- (sales.traverse(salesImpl.save) >> salesImpl.printReport).attempt
    finalDrinksSold <- drinksSoldRef.get
    finalStatementsPrinted <- statementsPrintedRef.get
    finalState = initialState.copy(finalDrinksSold, finalStatementsPrinted)
  yield (result, finalState)

  final private case class TestCase(
      initialState: SalesState,
      sales: List[Sale],
      expectedFinalState: SalesState,
  )

  private def testCaseGen = for
    sales <- Gen.containerOf[List, Sale](drinkGen.map { drink =>
      val price = InMemoryPrices.unsafeHowMuchForA(drink)
      Sale(drink, price)
    })
    initialState = SalesState.empty
    expectedFinalState = initialState.copy(
      drinksSold = initialState.drinksSold.setSales(sales.reverse),
      statementsPrinted = initialState.statementsPrinted.setStatements(
        (List(reportHeader) ++ statementsFrom(sales)).reverse,
      ),
    )
  yield TestCase(initialState, sales, expectedFinalState)
