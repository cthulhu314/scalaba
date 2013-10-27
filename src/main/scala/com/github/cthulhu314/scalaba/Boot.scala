package com.github.cthulhu314.scalaba

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import com.github.cthulhu314.scalaba.actors.RepositoryActor
import com.github.cthulhu314.scalaba.persistance.{MongoRepository, SlickRepository}

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("scalaba")
  implicit val executionContext = system.dispatcher

  val dbActor = system.actorOf(Props(new RepositoryActor(new MongoRepository(""))))
  // create and start our service actor
  val service = system.actorOf(Props(new ScalabaActor(dbActor)), "scalaba-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}