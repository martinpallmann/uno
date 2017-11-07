package uno
package game

sealed trait Event
case class GameCreated() extends Event
case class GameJoined(player: Player) extends Event
case class GameStarted(deck: Deck) extends Event
case class CardPlayed(player: Player, card: Card) extends Event
case class WrongCardPlayed(player: Player, card: Card) extends Event
