package es.eriktorr.coffee_machine
package domain

import domain.Command.CustomerOrder
import domain.Command.MessageDelivery
import domain.DrinkMakerSuite.{makeDrinkWith, testCaseGen, DrinkMakerState}
import infrastructure.*
import infrastructure.CoffeeMachineGenerators.{commandGen, paymentGen}
import infrastructure.InMemoryPrices

import cats.effect.IO
import cats.effect.kernel.Ref
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.Gen
import org.scalacheck.effect.PropF.forAllF
import squants.market.Money
import squants.market.MoneyConversions.*

final class DrinkMakerSuite extends CatsEffectSuite with ScalaCheckEffectSuite:

  test("it should get orders from customers") {
    forAllF(testCaseGen)(testCase =>
      makeDrinkWith(testCase.initialState)(testCase.command, testCase.payment).map {
        case (result, finalState) =>
          assert(result.isRight)
          assertEquals(finalState, testCase.expectedFinalState)
      },
    )
  }

object DrinkMakerSuite:
  final private case class DrinkMakerState(
      beverageAvailability: BeverageAvailability,
      missingDrinkNotificationsSent: MissingDrinkNotificationsSent,
      messagesShown: MessagesShown,
      drinksSold: DrinksSold,
  ):
    def setAvailableDrinks(newAvailableDrinks: List[Drink]): DrinkMakerState =
      copy(beverageAvailability = beverageAvailability.setAvailableDrinks(newAvailableDrinks))

  private object DrinkMakerState:
    val empty: DrinkMakerState =
      DrinkMakerState(
        BeverageAvailability.empty,
        MissingDrinkNotificationsSent.empty,
        MessagesShown.empty,
        DrinksSold.empty,
      )

  private def makeDrinkWith(initialState: DrinkMakerState)(command: Command, payment: Money) =
    for
      beverageAvailabilityRef <- Ref.of[IO, BeverageAvailability](initialState.beverageAvailability)
      missingDrinkNotificationsSentRef <- Ref.of[IO, MissingDrinkNotificationsSent](
        initialState.missingDrinkNotificationsSent,
      )
      messagesShownRef <- Ref.of[IO, MessagesShown](initialState.messagesShown)
      drinksSoldRef <- Ref.of[IO, DrinksSold](initialState.drinksSold)
      drinkMaker = DrinkMaker.impl(
        FakeBeverageQuantityChecker(beverageAvailabilityRef),
        FakeEmailNotifier(missingDrinkNotificationsSentRef),
        FakeMessageDisplay(messagesShownRef),
        InMemoryPrices,
        FakeSales(drinksSoldRef),
      )
      result <- drinkMaker.make(command, payment).attempt
      finalBeverageAvailability <- beverageAvailabilityRef.get
      finalMissingDrinkNotificationsSent <- missingDrinkNotificationsSentRef.get
      finalMessagesShown <- messagesShownRef.get
      finalDrinksSold <- drinksSoldRef.get
      finalState = initialState.copy(
        finalBeverageAvailability,
        finalMissingDrinkNotificationsSent,
        finalMessagesShown,
        finalDrinksSold,
      )
    yield (result, finalState)

  final private case class TestCase(
      initialState: DrinkMakerState,
      command: Command,
      payment: Money,
      expectedFinalState: DrinkMakerState,
  )

  private def testCaseGen = for
    command <- commandGen
    (payment, messages, sales) <- command match
      case CustomerOrder(drink, _, _, _) =>
        val price = InMemoryPrices.unsafeHowMuchForA(drink)
        paymentGen(price).map((_, List.empty[Message], List(Sale(drink, price))))
      case MessageDelivery(message) => Gen.const((0.EUR, List(message), List.empty[Sale]))
    initialState = DrinkMakerState.empty.setAvailableDrinks(Drink.allDrinks)
    expectedFinalState = initialState.copy(
      drinksSold = initialState.drinksSold.setSales(sales),
      messagesShown = initialState.messagesShown.setMessages(messages),
    )
  yield TestCase(initialState, command, payment, expectedFinalState)
