package es.eriktorr.coffee_machine
package domain

import domain.CoffeeMachineError.InvalidCustomerOrder
import infrastructure.CoffeeMachineGenerators.commandGen

import cats.syntax.all.*
import munit.ScalaCheckSuite
import org.scalacheck.Prop.*

final class CommandSuite extends ScalaCheckSuite:

  property("instructions are received in plain text") {
    forAll(commandGen) { command =>
      val text = command.show
      val result = Command.from(text)
      assertEquals(result, command.validNec[CoffeeMachineError])
    }
  }

  test("it should fail when the command cannot be recognised") {
    assertEquals(
      Command.from("Any"),
      InvalidCustomerOrder("Invalid format").invalidNec[Command],
    )
  }

  test("it should fail with an error when orange juice is ordered extra hot") {
    assertEquals(
      Command.from("Oh::"),
      InvalidCustomerOrder(s"Cannot make extra hot ${Drink.OrangeJuice.name}").invalidNec[Command],
    )
  }
