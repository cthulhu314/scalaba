package com.github.cthulhu314.scalaba

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import com.github.cthulhu314.scalaba.actors.{FileRepositoryActor, RepositoryActor}
import com.github.cthulhu314.scalaba.persistance.{PerformanceLoggingRepositoryDecorator, MongoRepository, SlickRepository}
import akka.routing.RoundRobinRouter
import com.github.cthulhu314.scalaba.persistance.files.NopFileRepository

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("scalaba")
  implicit val executionContext = system.dispatcher
  val repository = new PerformanceLoggingRepositoryDecorator(new MongoRepository("","scalaba"))
  val dbActor = system.actorOf(Props(new RepositoryActor(repository))
    .withRouter(RoundRobinRouter(5)))
  val fileActor = system.actorOf(Props(new FileRepositoryActor(new NopFileRepository())))
  // create and start our service actor
  val service = system.actorOf(Props(new ScalabaActor(dbActor,fileActor)), "scalaba-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}