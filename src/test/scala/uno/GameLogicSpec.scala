package uno

import uno.game._
import utest._

object GameLogicSpec extends TestSuite {

  override def tests = Tests {

    'idle - {

      implicit val startState: State = Idle

      * - {
        CreateGame(Player("John")) ~~>
          () ==>
          ok(GameCreated(), GameJoined(Player("John")))
      }
    }

    'waitingForPlayers - {

      implicit val startState: State = WaitingForPlayers(Players())

      * - {
        JoinGame(Player("Alice")) ~~>
          () ==>
          ok(GameJoined(Player("Alice")))
      }
      * - {
        StartGame() ~~>
          () ==>
          Left(UnknownCommandError(startState, StartGame()))
      }
    }

    'waitingToStart - {

      implicit val startState: State =
        WaitingToStart(Players(Player("John") :: Player("Alice") :: Player("Bob") :: Nil))

      * - {
        JoinGame(Player("Jane")) ~~>
          () ==>
          ok(GameJoined(Player("Jane")))
      }

      * - {
        StartGame() ~~>
          () ==>
          ok(GameStarted())
      }
    }

    'playing - {

      implicit val startState: State =
        Playing(Players(Player("John") :: Player("Alice") :: Player("Bob") :: Nil))

      * - {
        JoinGame(Player("Jane")) ~~>
          () ==>
          Left(UnknownCommandError(startState, JoinGame(Player("Jane"))))
      }
    }
  }
}
