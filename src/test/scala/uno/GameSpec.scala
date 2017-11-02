package uno

import java.util.UUID
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import uno.game._
import scala.concurrent.ExecutionContext.Implicits._
import utest._

object GameSpec extends TestSuite {
  override def tests = Tests {
    'integrationTest - {
      implicit val system: ActorSystem = ActorSystem("main", ConfigFactory.defaultApplication())
      val game = new Game(UUID.randomUUID().toString)
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
        d == Right(GameStarted() :: Nil)
      )
    }
  }
}
