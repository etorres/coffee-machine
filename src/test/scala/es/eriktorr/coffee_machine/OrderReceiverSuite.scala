package es.eriktorr.coffee_machine

import cats.effect._
import eu.timepit.refined.auto._
import weaver._

object OrderReceiverSuite extends IOSuite {
  override type Res = OrderReceiver[IO]

  override def sharedResource: Resource[IO, Res] = OrderReceiver.impl[IO].toResource

  test("Drink maker makes 1 tea with 1 sugar and a stick") { orderReceiver =>
    for {
      order <- orderReceiver.orderFrom(Command("T:1:0"))
    } yield expect(order == DrinkOrder(Tea, Sugar(1), Stick(true)))
  }

  test("Drink maker makes 1 chocolate with no sugar and therefore no stick") { orderReceiver =>
    for {
      order <- orderReceiver.orderFrom(Command("H::"))
    } yield expect(order == DrinkOrder(Chocolate, Sugar(0), Stick(false)))
  }

  test("Drink maker makes 1 coffee with 2 sugars and a stick") { orderReceiver =>
    for {
      order <- orderReceiver.orderFrom(Command("C:2:0"))
    } yield expect(order == DrinkOrder(Coffee, Sugar(2), Stick(true)))
  }

  test(
    "Drink maker forwards any message received onto the coffee machine interface for the customer to see"
  ) { orderReceiver =>
    for {
      order <- orderReceiver.orderFrom(Command("M:message-content"))
    } yield expect(order == CoffeeMachineMessage("message-content"))
  }
}
