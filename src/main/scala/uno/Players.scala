package uno

import uno.Players._

final case class Players(value: List[Player], last: Int, direction: Direction) {

  def +(p: Player): Players = this.copy(p :: value)

  def lastPlayed(player: Player): Players = this.copy(last = value.indexOf(player))

  def isNext(player: Player): Boolean = direction match {
    case Forward => value.indexOf(player) == (last + 1) % value.size
    case Back    => value.indexOf(player) == (last + value.size - 1) % value.size
  }
}

object Players {

  sealed trait Direction
  case object Forward extends Direction
  case object Back extends Direction

  def apply(): Players = new Players(Nil, 0, Forward)

  object twoPlayers {
    def unapply(players: Players): Boolean =
      players.value.size == 2
  }
}
