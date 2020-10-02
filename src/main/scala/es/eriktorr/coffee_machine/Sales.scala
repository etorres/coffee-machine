package es.eriktorr.coffee_machine

import cats.effect._
import cats.effect.concurrent.Ref
import cats.syntax.all._
import squants.market.Money

final case class Sale(drink: Drink, profit: Money)

trait Sales[F[_]] {
  def save(sale: Sale): F[Unit]
  def printReport: F[Unit]
}

object Sales {
  implicit def apply[F[_]](implicit ev: Sales[F]): Sales[F] = ev

  def impl[F[_]: Sync](ref: Ref[F, List[Sale]]): Sales[F] = new Sales[F] {

    override def save(sale: Sale): F[Unit] = ref.get.flatMap(current => ref.set(sale :: current))

    override def printReport: F[Unit] = ???
  }
}

// Total revenue
