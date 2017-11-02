package uno.event

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.ClassTag
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout

abstract class EventSourceAggregate[STATE, COMMAND, EVENT <: AnyRef, ERROR]
(implicit system: ActorSystem, ect: ClassTag[EVENT], sct: ClassTag[STATE], cct: ClassTag[COMMAND]) {

  implicit val timeout: Timeout = Timeout(10.seconds)

  protected val persistenceId: String

  protected val eventSource: EventSource[STATE, COMMAND, EVENT, ERROR]

  private lazy val actor = EventSourceActor(persistenceId, eventSource)

  def send(command: COMMAND): Future[Either[ERROR, List[EVENT]]] =
    (actor ? command).mapTo[Either[ERROR, List[EVENT]]]
}
