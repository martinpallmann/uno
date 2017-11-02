import uno.game.{Command, Error, Event, State}

package object uno {

  def ok(events: Event*): Either[Error, List[Event]] = Right(events.toList)

  implicit class EnhancedCmd(command: Command)(implicit startState: State) {
    def ~~> (xs: Event*): Either[Error, List[Event]] = {
      val state = xs.foldLeft(startState) {
        (acc, elem) ⇒ GameLogic.evolve(acc, elem)
      }
      GameLogic.decide(state, command)
    }
  }
}
