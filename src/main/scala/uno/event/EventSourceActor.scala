package uno.event

import scala.reflect.ClassTag
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}

class EventSourceActor[STATE, COMMAND, EVENT <: AnyRef, ERROR](
    override val persistenceId: String,
    startState: STATE,
    decide: (STATE, COMMAND) ⇒ Either[ERROR, List[EVENT]],
    evolve: (STATE, EVENT) ⇒ STATE
)(implicit ect: ClassTag[EVENT], sct: ClassTag[STATE], cct: ClassTag[COMMAND]) extends PersistentActor {

  private var state: STATE = startState

  private def evolveState(event: EVENT): Unit =
    state = evolve(state, event)

  private val snapShotInterval = 1000

  final override def receiveCommand: Receive = {
    case c: COMMAND ⇒
      val answerTo = sender()
      decide(state, c) match {
        case result @ Right(events) ⇒
          events.foreach(
            event ⇒ {
              persist(event)(evolveState)
              context.system.eventStream.publish(event)
              if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) saveSnapshot(state)
            }
          )
          answerTo ! result
        case result @ Left(_) ⇒
          answerTo ! result
      }
  }

  final override def receiveRecover: Receive = {
    case event: EVENT ⇒ evolveState(event)
    case SnapshotOffer(_, snapshot: STATE) ⇒ state = snapshot
  }
}

object EventSourceActor {
  def props[STATE, COMMAND, EVENT <: AnyRef, ERROR](
      persistenceId: String,
      startState: STATE,
      decide: (STATE, COMMAND) ⇒ Either[ERROR, List[EVENT]],
      evolve: (STATE, EVENT) ⇒ STATE,
      ect: ClassTag[EVENT],
      sct: ClassTag[STATE],
      cct: ClassTag[COMMAND]): Props =
    Props(new EventSourceActor[STATE, COMMAND, EVENT, ERROR](
      persistenceId, startState, decide, evolve)(ect, sct, cct)
    )

  def apply[STATE, COMMAND, EVENT <: AnyRef, ERROR](
      persistenceId: String,
      startState: STATE,
      decide: (STATE, COMMAND) ⇒ Either[ERROR, List[EVENT]],
      evolve: (STATE, EVENT) ⇒ STATE
  )(implicit system: ActorSystem, ect: ClassTag[EVENT], sct: ClassTag[STATE], cct: ClassTag[COMMAND]): ActorRef =
    system.actorOf(props(persistenceId, startState, decide, evolve, ect, sct, cct))
}
