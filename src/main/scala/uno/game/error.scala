package uno.game

sealed trait Error
case class UnknownCommandError(state: State, command: Command) extends Error
