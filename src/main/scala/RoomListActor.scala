package fr.cytech.icc

import org.apache.pekko.actor.typed.{ ActorRef, Behavior }
import org.apache.pekko.actor.typed.scaladsl.Behaviors

enum RoomListMessage {
  case CreateRoom(name: String)
  case GetRoom(name: String, replyTo: ActorRef[Option[ActorRef[Message]]])
}

object RoomListActor {

  import RoomListMessage.*

  def apply(rooms: Map[String, ActorRef[Message]] = Map.empty): Behavior[RoomListMessage] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case CreateRoom(name)       => 
          if (rooms.contains(name)) {
            Behaviors.same
          } else {
            val actor = context.spawn(RoomActor(name), name)
            val newRoom = (name,actor)
            apply(rooms + newRoom)
          }


        case GetRoom(name, replyTo) => 
          replyTo ! rooms.get(name)
          Behaviors.same
      }
    }
  }
}
