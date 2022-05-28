package es.eriktorr.coffee_machine
package infrastructure

import domain.*
import domain.Command.{CustomerOrder, MessageDelivery}
import domain.Drink.OrangeJuice

import org.scalacheck.{Arbitrary, Gen}
import squants.market.Money

object CoffeeMachineGenerators:
  val customerOrderGen: Gen[CustomerOrder] = for
    drink <- Gen.oneOf(Drink.values.toList)
    extraHot <-
      if drink == OrangeJuice then Gen.const(ExtraHot(false))
      else Arbitrary.arbBool.arbitrary.map(ExtraHot.apply)
    sugar <- Gen.oneOf(Sugar.values.toList)
    stick = Stick(sugar.amount > 0)
  yield CustomerOrder(drink, extraHot, sugar, stick)

  private[this] val messageDeliveryGen = textGen().map(x => MessageDelivery(Message.unsafeFrom(x)))

  private[this] def textGen(minLength: Int = 3, maxLength: Int = 10): Gen[String] = for
    length <- Gen.choose(minLength, maxLength)
    text <- Gen.listOfN[Char](length, Gen.alphaNumChar).map(_.mkString)
  yield text

  val commandGen: Gen[Command] = Gen.frequency(9 -> customerOrderGen, 1 -> messageDeliveryGen)

  def paymentGen(price: Money): Gen[Money] =
    Gen.choose(price.amount, BigDecimal(50)).map(Money(_, price.currency))

  def underpaymentGen(price: Money): Gen[Money] =
    Gen.choose(BigDecimal(0), price.amount - 0.01d).map(Money(_, price.currency))
