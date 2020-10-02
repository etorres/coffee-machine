package es.eriktorr.coffee_machine

import cats.effect._
import cats.effect.concurrent.Ref
import squants.market.MoneyConversions._
import weaver.IOSuite

object SalesSuite extends IOSuite {
  private[this] val ref: Ref[IO, List[Sale]] = Ref.unsafe[IO, List[Sale]](List.empty)

  override type Res = Sales[IO]

  override def sharedResource: Resource[IO, Res] = Sales.impl[IO](ref).toResource

  test("Save a sale") { sales =>
    val sale = Sale(Coffee, 0.6.EUR)
    for {
      _ <- sales.save(sale)
      currentSales <- ref.get
    } yield expect(currentSales == List(sale))
  }
}
