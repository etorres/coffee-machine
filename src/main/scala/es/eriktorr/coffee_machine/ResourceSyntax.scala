package es.eriktorr.coffee_machine

import cats.effect._

trait ResourceSyntax {
  implicit class ResourceOps[A](self: A) {
    def toResource: Resource[IO, A] = Resource.make(IO(self))(_ => IO.unit)
  }
}
