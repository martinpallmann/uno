package uno
package game

import akka.serialization.SerializerWithStringManifest

sealed trait Event
case class GameCreated() extends Event
case class GameJoined(player: Player) extends Event
case class GameStarted() extends Event
case class CardPlayed() extends Event

class EventSerializer extends SerializerWithStringManifest {

  override def identifier = 1234567

  override def manifest(o: AnyRef): String = o match {
    case _: GameCreated ⇒ "game_created"
    case _: GameJoined  ⇒ "game_joined"
    case _: GameStarted ⇒ "game_started"
    case _: CardPlayed  ⇒ "card_played"
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case _: GameCreated ⇒ "".getBytes("UTF-8")
    case _: GameJoined  ⇒ "".getBytes("UTF-8")
    case _: GameStarted ⇒ "".getBytes("UTF-8")
    case _: CardPlayed  ⇒ "".getBytes("UTF-8")
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): Event = manifest match {
    case "game_created" ⇒ GameCreated()
    case "game_joined"  ⇒ GameJoined(Player())
    case "game_started" ⇒ GameStarted()
    case "card_played"  ⇒ CardPlayed()
  }
}
