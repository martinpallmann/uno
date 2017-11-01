package uno

import java.util.UUID
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import uno.game._
import scala.concurrent.ExecutionContext.Implicits._
import utest._

object GameSpec extends TestSuite {
  override def tests = Tests {
    'test1 - {
      implicit val system: ActorSystem = ActorSystem("main", ConfigFactory.defaultApplication())
      val game = new Game("id")
      for {
        a ← game.send(CreateGame(Player()))
        b ← game.send(JoinGame(Player()))
        c ← game.send(JoinGame(Player()))
        d ← game.send(StartGame())
        _ ← system.terminate()
      } yield assert(
        a == Right(GameCreated() :: GameJoined(Player()) :: Nil),
        b == Right(GameJoined(Player()) :: Nil),
        c == Right(GameJoined(Player()) :: Nil),
        d == Right(GameStarted() :: Nil)
      )
    }
  }
}
