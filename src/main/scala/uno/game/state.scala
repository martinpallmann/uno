package uno
package game

sealed trait State
case object Idle extends State
case class WaitingForPlayers(players: List[Player]) extends State
case class WaitingToStart(players: List[Player]) extends State
case class Playing() extends State
