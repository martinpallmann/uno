package uno
package game

import java.io.NotSerializableException
import akka.serialization.SerializerWithStringManifest

sealed trait Event
case class GameCreated() extends Event
case class GameJoined(player: Player) extends Event
case class GameStarted() extends Event
case class CardPlayed() extends Event

class EventSerializer extends SerializerWithStringManifest {

  override def identifier = 1234567

  override def manifest(o: AnyRef): String = o match {
    case _: GameCreated ⇒ "game_created_v1"
    case _: GameJoined  ⇒ "game_joined_v1"
    case _: GameStarted ⇒ "game_started_v1"
    case _: CardPlayed  ⇒ "card_played_v1"
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case _: GameCreated ⇒ "".getBytes("UTF-8")
    case GameJoined(p)  ⇒ p.name.getBytes("UTF-8")
    case _: GameStarted ⇒ "".getBytes("UTF-8")
    case _: CardPlayed  ⇒ "".getBytes("UTF-8")
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): Event = manifest match {
    case "game_created_v1" ⇒ GameCreated()
    case "game_joined_v1"  ⇒ GameJoined(Player(new String(bytes, "UTF-8")))
    case "game_started_v1" ⇒ GameStarted()
    case "card_played_v1"  ⇒ CardPlayed()
    case x                 ⇒ throw new NotSerializableException(x)
  }
}
