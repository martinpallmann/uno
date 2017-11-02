package uno
package game

sealed trait State
case object Idle extends State
case class WaitingForPlayers(players: Players) extends State
case class WaitingToStart(players: Players) extends State
case class Playing(players: Players) extends State
