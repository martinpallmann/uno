package uno
package game

sealed trait Command
case class CreateGame(player: Player) extends Command
case class JoinGame(player: Player) extends Command
case class StartGame() extends Command
case class PlayCard(player: Player, card: Card) extends Command
