package uno.event

trait EventSource[STATE, COMMAND, EVENT <: AnyRef, ERROR] {

  case class Cmd(value: COMMAND)

  val startState: STATE
  val decide: (STATE, COMMAND) ⇒ Either[ERROR, List[EVENT]]
  val evolve: (STATE, EVENT) ⇒ STATE

}
