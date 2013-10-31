package com.github.cthulhu314.scalaba.actors

import com.github.cthulhu314.scalaba.persistance.{SlickRepository, Repository}
import akka.actor._
import com.github.cthulhu314.scalaba.models.{Post,Thread}

trait RepositoryMessage

object RepositoryMessage {
  def applyTo(repository : Repository)(message : RepositoryMessage) = message match {
    case GetPost(id) => repository.getPost(id)
    case GetThread(id) =>  repository.getThread(id)
    case GetThreads(from,count) => repository.getThreads(from,count)
    case CreateThread(thread) => repository.create(thread)
    case CreatePost(post) => repository.create(post)
    case DeleteThread(id) => repository.delete(Thread(id = id,posts = Seq()))
    case DeletePost(id) => repository.delete(Post(Some(id),None,None,None,None,"",null))

  }
}

case class GetPost(id : Int) extends RepositoryMessage
case class GetThread(id : Int) extends RepositoryMessage
case class CreateThread(thread : Thread) extends RepositoryMessage
case class CreatePost(post : Post) extends RepositoryMessage
case class GetThreads(from : Int, count : Int) extends RepositoryMessage
case class DeleteThread(id : Int) extends RepositoryMessage
case class DeletePost(id : Int) extends RepositoryMessage

class RepositoryActor(repository : Repository) extends Actor  {
  import RepositoryMessage._
  def receive = {
    case x : RepositoryMessage => sender ! applyTo(repository)(x)
  }
}


