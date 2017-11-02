package uno

import scala.language.implicitConversions
import akka.actor.ActorSystem
import uno.Players.twoPlayers
import uno.event.EventSourceAggregate
import uno.game._

class Game(override val persistenceId: String)(implicit system: ActorSystem)
    extends EventSourceAggregate[State, Command, Event, Error] {

  override val startState: State = Game.startState

  override val decide: (State, Command) ⇒ Either[Error, List[Event]] = Game.decide

  override val evolve: (State, Event) => State = Game.evolve

}

object Game {
  val startState: State = Idle

  val decide: (State, Command) ⇒ Either[Error, List[Event]] = {

    case (Idle, CreateGame(p)) ⇒
      ok(GameCreated(), GameJoined(p))

    case (WaitingForPlayers(_), JoinGame(p)) ⇒
      ok(GameJoined(p))

    case (WaitingToStart(_), JoinGame(p)) ⇒
      ok(GameJoined(p))

    case (WaitingToStart(_), StartGame()) ⇒
      ok(GameStarted())

    case (state, command) ⇒
      UnknownCommandError(state, command)
  }

  val evolve: (State, Event) => State = {

    case (Idle, GameCreated()) ⇒
      WaitingForPlayers(Players())

    case (WaitingForPlayers(ps @ twoPlayers()), GameJoined(p)) ⇒
      WaitingToStart(ps + p)

    case (WaitingForPlayers(ps), GameJoined(p)) ⇒
      WaitingForPlayers(ps + p)

    case (WaitingToStart(ps), GameJoined(p)) ⇒
      Playing(ps + p)

    case (WaitingToStart(ps), GameStarted()) ⇒
      Playing(ps)

    case (state, _) ⇒
      state
  }

  private def ok(events: Event*): Either[Error, List[Event]] = Right(events.toList)

  private implicit def toEither(e: Error): Either[Error, List[Event]] = Left(e)
}
