package es.eriktorr.coffee_machine
package infrastructure

import domain.{Message, MessageDisplay}

import cats.effect.{IO, Ref}

final case class MessagesShown(messages: List[Message]):
  def setMessages(newMessages: List[Message]): MessagesShown = copy(newMessages)

object MessagesShown:
  val empty: MessagesShown = MessagesShown(List.empty)

final class FakeMessageDisplay(stateRef: Ref[IO, MessagesShown]) extends MessageDisplay:
  override def show(message: Message): IO[Unit] =
    stateRef.update(currentState => currentState.copy(message :: currentState.messages))
