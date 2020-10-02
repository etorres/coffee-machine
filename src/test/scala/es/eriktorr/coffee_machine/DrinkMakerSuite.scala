package es.eriktorr.coffee_machine

import cats.effect._
import es.eriktorr.coffee_machine.shared.infrastructure.FakeAppContext
import eu.timepit.refined.auto._
import weaver._
import squants.market.MoneyConversions._

object DrinkMakerSuite extends IOSuite with FakeAppContext {
  override type Res = DrinkMaker[IO]

  override def sharedResource: Resource[IO, Res] = DrinkMaker.impl[IO](appContext).toResource

  test("Drink maker makes 1 tea with 1 sugar and a stick") { drinkMaker =>
    for {
      order <- drinkMaker.make(appContext.priceOf(Tea), Command("T:1:0"))
    } yield expect(order == DrinkOrder(Tea, Sugar(1), Stick(true)))
  }

  test("Drink maker makes 1 chocolate with no sugar and therefore no stick") { drinkMaker =>
    for {
      order <- drinkMaker.make(appContext.priceOf(Chocolate), Command("H::"))
    } yield expect(order == DrinkOrder(Chocolate, Sugar(0), Stick(false)))
  }

  test("Drink maker makes 1 coffee with 2 sugars and a stick") { drinkMaker =>
    for {
      order <- drinkMaker.make(appContext.priceOf(Coffee), Command("C:2:0"))
    } yield expect(order == DrinkOrder(Coffee, Sugar(2), Stick(true)))
  }

  test(
    "Drink maker forwards any message received onto the coffee machine interface for the customer to see"
  ) { drinkMaker =>
    for {
      order <- drinkMaker.make(0.EUR, Command("M:message-content"))
    } yield expect(order == CoffeeMachineMessage("message-content"))
  }

  test("The drink maker should make the drinks only if the correct amount of money is given") {
    drinkMaker =>
      for {
        order <- drinkMaker.make(0.EUR, Command("C:2:0"))
      } yield expect(
        order == CoffeeMachineMessage(
          s"Not enough money, missing ${appContext.priceOf(Coffee).toString}"
        )
      )
  }
}
