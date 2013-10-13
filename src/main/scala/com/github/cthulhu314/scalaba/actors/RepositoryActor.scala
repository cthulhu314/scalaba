package com.github.cthulhu314.scalaba.actors

import com.github.cthulhu314.scalaba.persistance.Repository
import akka.actor._
import com.github.cthulhu314.scalaba.models.{Post,Thread}

case class GetPost(id : Int)
case class GetThread(id : Int)
case class CreateThread(thread : Thread)
case class CreatePost(post : Post)
case class GetThreads(from : Int, count : Int)

abstract class RepositoryActor extends Actor with Repository  {


  def receive = {
    case GetPost(id) => sender ! getPost(id)
    case GetThread(id) => sender ! getThread(id)
    case GetThreads(from,count) => sender ! getThreads(from,count)
  }
}
