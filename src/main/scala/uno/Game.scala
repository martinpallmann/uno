package uno

import akka.actor.ActorSystem
import uno.event.{EventSource, EventSourceAggregate}
import uno.game.{Command, Error, Event, State}

class Game(override val persistenceId: String)(implicit system: ActorSystem)
    extends EventSourceAggregate[State, Command, Event, Error] {

  override protected val eventSource: EventSource[State, Command, Event, Error] =
    new EventSource[State, Command, Event, Error] {

    override val startState: State =
      GameLogic.startState

    override val decide: (State, Command) ⇒ Either[Error, List[Event]] =
      GameLogic.decide

    override val evolve: (State, Event) => State =
      GameLogic.evolve
  }
}
