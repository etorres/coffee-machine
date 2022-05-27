package es.eriktorr.coffee_machine
package domain

import cats.effect.IO
import squants.market.Money

final case class Sale(drink: Drink, earned: Money)

trait Sales:
  def save(sale: Sale): IO[Unit]
  def printReport: IO[Unit]
