package uno

import java.util.UUID

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import uno.game._

import scala.concurrent.ExecutionContext.Implicits._
import utest._

import Predef.genericWrapArray
import Predef.identity

object GameSpec extends TestSuite {

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
        WaitingToStart(Players(Player("John") :: Player("Alice") :: Player("Bob") :: Nil, 0, Players.Forward))

      * - {
        JoinGame(Player("Jane")) ~~>
            () ==>
            ok(GameJoined(Player("Jane")))
      }

      * - {
        StartGame() ~~>
            () ==>
            ok(GameStarted(Deck()))
      }
    }

    'playing - {

      implicit val startState: State =
        Playing(Players(Player("John") :: Player("Alice") :: Player("Bob") :: Nil, 0, Players.Forward), Deck(), Card.Wild)

      * - {
        JoinGame(Player("Jane")) ~~>
            () ==>
            Left(UnknownCommandError(startState, JoinGame(Player("Jane"))))
      }
    }

    'integrationTest - {
      implicit val system: ActorSystem = ActorSystem("main", ConfigFactory.defaultApplication())
      val game = new Game(UUID.randomUUID().toString, identity)
      val deck = Deck()
      for {
        a ← game.send(CreateGame(Player("John")))
        b ← game.send(JoinGame(Player("Alice")))
        c ← game.send(JoinGame(Player("Bob")))
        d ← game.send(StartGame())
        _ ← system.terminate()
      } yield assert(
        a == Right(GameCreated() :: GameJoined(Player("John")) :: Nil),
        b == Right(GameJoined(Player("Alice")) :: Nil),
        c == Right(GameJoined(Player("Bob")) :: Nil),
        d == Right(GameStarted(deck) :: Nil)
      )
    }
  }
}
