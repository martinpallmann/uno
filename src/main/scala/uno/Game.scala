package uno

import scala.language.implicitConversions
import akka.actor.ActorSystem
import uno.Deck.Shuffle
import uno.Players.twoPlayers
import uno.event.EventSourceAggregate
import uno.game._

class Game(override val persistenceId: String, shuffle: Shuffle)(implicit system: ActorSystem)
    extends EventSourceAggregate[State, Command, Event, Error] {

//  val shuffle: Shuffle = deck => Deck(deck.cards.sortBy(_ => Random.nextFloat()))

  override val startState: State = Game.startState

  override val decide: (State, Command) ⇒ Either[Error, List[Event]] = Game.decide(shuffle)

  override val evolve: (State, Event) ⇒ State = Game.evolve

}

object Game {
  val startState: State = Idle

  val decide: Shuffle => (State, Command) ⇒ Either[Error, List[Event]] = shuffle => {

    case (Idle, CreateGame(p)) ⇒
      ok(GameCreated(), GameJoined(p))

    case (WaitingForPlayers(_), JoinGame(p)) ⇒
      ok(GameJoined(p))

    case (WaitingToStart(_), JoinGame(p)) ⇒
      ok(GameJoined(p))

    case (WaitingToStart(_), StartGame()) ⇒
      ok(GameStarted(shuffle(Deck())))

    case (Playing(ps, _, l), PlayCard(p, c)) ⇒
      if (ps.isNext(p) || c == l) ok(CardPlayed(p, c))
      else ok(WrongCardPlayed(p, c))

    case (state, command) ⇒
      UnknownCommandError(state, command)
  }

  val evolve: (State, Event) ⇒ State = {

    case (Idle, GameCreated()) ⇒
      WaitingForPlayers(Players())

    case (WaitingForPlayers(ps @ twoPlayers()), GameJoined(p)) ⇒
      WaitingToStart(ps + p)

    case (WaitingForPlayers(ps), GameJoined(p)) ⇒
      WaitingForPlayers(ps + p)

    case (WaitingToStart(ps), GameJoined(p)) ⇒
      WaitingToStart(ps + p)

    case (WaitingToStart(ps), GameStarted(d)) ⇒ d.draw match {
      case (c, d1) => Playing(ps, d1, c)
    }

    case (Playing(ps, d, _), CardPlayed(p, c)) =>
      Playing(ps.lastPlayed(p), d, c)

    case (Playing(ps, d, _), WrongCardPlayed(p, c)) =>
      Playing(ps.lastPlayed(p), d, c)

    case (state, _) ⇒
      state
  }

  private def ok(events: Event*): Either[Error, List[Event]] = Right(events.toList)

  private implicit def toEither(e: Error): Either[Error, List[Event]] = Left(e)
}
