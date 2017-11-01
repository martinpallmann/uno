package uno

import uno.game._

object GameLogic {

  val startState: State = Idle

  val decide: (State, Command) ⇒ Either[Error, List[Event]] = {

    case (Idle, CreateGame(p)) ⇒
      Right(List(GameCreated(), GameJoined(p)))

    case (WaitingForPlayers(_), JoinGame(p)) ⇒
      Right(List(GameJoined(p)))

    case (WaitingToStart(_), StartGame()) ⇒
      Right(List(GameStarted()))

    case (state, command) ⇒
      Left(UnknownCommandError(state, command))
  }

  val evolve: (State, Event) => State = {

    case (Idle, GameCreated()) ⇒
      WaitingForPlayers(Nil)

    case (WaitingForPlayers(p1 :: p2 :: Nil), GameJoined(p)) ⇒
      WaitingToStart(p :: p1 :: p2 :: Nil)

    case (WaitingForPlayers(ps), GameJoined(p)) ⇒
      WaitingForPlayers(p :: ps)

    case (WaitingToStart(p), GameStarted()) ⇒
      Playing()

    case (state, _) ⇒
      state
  }
}
