package uno.event

trait EventSource[STATE, COMMAND, EVENT <: AnyRef, ERROR] {
  val startState: STATE
  val decide: (STATE, COMMAND) ⇒ Either[ERROR, List[EVENT]]
  val evolve: (STATE, EVENT) ⇒ STATE
}
