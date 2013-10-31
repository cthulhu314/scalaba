package com.github.cthulhu314.scalaba.actors

import akka.actor._
import com.github.cthulhu314.scalaba.persistance.boards.Boards

case class Intention(board : String, action : RepositoryMessage)

class BoardsActor(board : Boards) extends Actor {
  import RepositoryMessage._
    def receive: Actor.Receive = {
      case Intention(name,action) => board.get(name).foreach { repository =>
        sender ! applyTo(repository)(action)
      }
  }
}
