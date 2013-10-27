package com.github.cthulhu314.scalaba.actors

import com.github.cthulhu314.scalaba.persistance.{SlickRepository, Repository}
import akka.actor._
import com.github.cthulhu314.scalaba.models.{Post,Thread}

case class GetPost(id : Int)
case class GetThread(id : Int)
case class CreateThread(thread : Thread)
case class CreatePost(post : Post)
case class GetThreads(from : Int, count : Int)

class RepositoryActor(repository : Repository) extends Actor  {


  def receive = {
    case GetPost(id) => sender ! repository.getPost(id)
    case GetThread(id) => sender ! repository.getThread(id)
    case GetThreads(from,count) => sender ! repository.getThreads(from,count)
    case CreateThread(thread) => sender ! repository.create(thread)
    case CreatePost(post) => sender ! repository.create(post)
  }
}

