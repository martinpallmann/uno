package uno.event

import scala.reflect.ClassTag
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}

class EventSourceActor[STATE, COMMAND, EVENT <: AnyRef, ERROR](
    override val persistenceId: String,
    es: EventSource[STATE, COMMAND, EVENT, ERROR]
)(implicit ect: ClassTag[EVENT], sct: ClassTag[STATE]) extends PersistentActor {

  private var state: STATE = es.startState

  private def evolveState(event: EVENT): Unit =
    state = es.evolve(state, event)

  private val snapShotInterval = 1000

  final override def receiveCommand: Receive = {
    case es.Cmd(c) ⇒
      val answerTo = sender()
      es.decide(state, c) match {
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
      eventSource: EventSource[STATE, COMMAND, EVENT, ERROR],
      ect: ClassTag[EVENT],
      sct: ClassTag[STATE]): Props =
    Props(new EventSourceActor[STATE, COMMAND, EVENT, ERROR](persistenceId, eventSource)(ect, sct))

  def apply[STATE, COMMAND, EVENT <: AnyRef, ERROR](
      persistenceId: String,
      eventSource: EventSource[STATE, COMMAND, EVENT, ERROR]
  )(implicit system: ActorSystem, ect: ClassTag[EVENT], sct: ClassTag[STATE]): ActorRef =
    system.actorOf(props(persistenceId, eventSource, ect, sct))
}
