package uno

final case class Players(value: List[Player] = Nil) {
  def +(p: Player): Players = this.copy(p :: value)
}

object Players {
  object twoPlayers {
    def unapply(players: Players): Boolean = {
      players.value.size == 2
    }
  }
}
