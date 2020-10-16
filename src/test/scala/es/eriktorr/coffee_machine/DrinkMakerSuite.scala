package es.eriktorr.coffee_machine

import cats.effect._
import cats.effect.concurrent.Ref
import es.eriktorr.coffee_machine.Drink._
import es.eriktorr.coffee_machine.DrinkMakerOrder._
import es.eriktorr.coffee_machine.shared.infrastructure.{
  FakeAppContext,
  FakeBeverageQuantityChecker,
  FakeEmailNotifier,
  FakeStatementsPrinter
}
import eu.timepit.refined.auto._
import weaver._
import squants.market.MoneyConversions._

object DrinkMakerSuite extends IOSuite with FakeAppContext {
  override type Res = DrinkMaker[IO]

  override def sharedResource: Resource[IO, Res] =
    DrinkMaker
      .impl[IO](
        appContext,
        new FakeBeverageQuantityChecker(List.empty),
        new FakeEmailNotifier(Ref.unsafe[IO, List[Drink]](List.empty)),
        Sales.impl[IO](
          Ref.unsafe[IO, List[Sale]](List.empty),
          new FakeStatementsPrinter(
            Ref.unsafe[IO, List[Statement]](List.empty)
          )
        )
      )
      .toResource

  test("Drink maker makes 1 tea with 1 sugar and a stick") {
    _.make(appContext.priceOf(Tea), Command("T:1:0")).map { order =>
      expect(order == DrinkOrder(Tea, Sugar(1), Stick(true), ExtraHot(false)))
    }
  }

  test("Drink maker makes 1 chocolate with no sugar and therefore no stick") {
    _.make(appContext.priceOf(Chocolate), Command("H::")).map { order =>
      expect(order == DrinkOrder(Chocolate, Sugar(0), Stick(false), ExtraHot(false)))
    }
  }

  test("Drink maker makes 1 coffee with 2 sugars and a stick") {
    _.make(appContext.priceOf(Coffee), Command("C:2:0")).map { order =>
      expect(order == DrinkOrder(Coffee, Sugar(2), Stick(true), ExtraHot(false)))
    }
  }

  test(
    "Drink maker forwards any message received onto the coffee machine interface for the customer to see"
  ) {
    _.make(0.EUR, Command("M:message-content")).map { order =>
      expect(order == CoffeeMachineMessage("message-content"))
    }
  }

  test("The drink maker should make the drinks only if the correct amount of money is given") {
    _.make(0.EUR, Command("C:2:0")).map { order =>
      expect(
        order == CoffeeMachineMessage(
          s"Not enough money, missing ${appContext.priceOf(Coffee).toString}"
        )
      )
    }
  }

  test("Drink maker will make one orange juice") {
    _.make(appContext.priceOf(OrangeJuice), Command("O::")).map { order =>
      expect(order == DrinkOrder(OrangeJuice, Sugar(0), Stick(false), ExtraHot(false)))
    }
  }

  test("Drink maker will make an extra hot coffee with no sugar") {
    _.make(appContext.priceOf(Coffee), Command("Ch::")).map { order =>
      expect(order == DrinkOrder(Coffee, Sugar(0), Stick(false), ExtraHot(true)))
    }
  }

  test("Drink maker will make an extra hot chocolate with one sugar and a stick") {
    _.make(appContext.priceOf(Chocolate), Command("Hh:1:0")).map { order =>
      expect(order == DrinkOrder(Chocolate, Sugar(1), Stick(true), ExtraHot(true)))
    }
  }

  test("The drink maker will make an extra hot tea with two sugar and a stick") {
    _.make(appContext.priceOf(Tea), Command("Th:2:0")).map { order =>
      expect(order == DrinkOrder(Tea, Sugar(2), Stick(true), ExtraHot(true)))
    }
  }
}
