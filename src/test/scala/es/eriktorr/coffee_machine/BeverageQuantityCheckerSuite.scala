package es.eriktorr.coffee_machine

import es.eriktorr.coffee_machine.shared.infrastructure.FakeAppContext
import eu.timepit.refined.auto._
import weaver.SimpleIOSuite

object BeverageQuantityCheckerSuite extends SimpleIOSuite with FakeAppContext {
  simpleTest("The coffee machine informs me the shortage and that a notification has been sent") {
    val (drinkMaker, emailNotifier, _, _, _) = testScenario(List(Coffee))
    for {
      order <- drinkMaker.make(appContext.priceOf(Coffee), Command("C::"))
      notifications <- emailNotifier.ref.get
    } yield expect(
      order == CoffeeMachineMessage(s"I ran out of Coffee. My human was notified already") && notifications == List(
        Coffee
      )
    )
  }
}
