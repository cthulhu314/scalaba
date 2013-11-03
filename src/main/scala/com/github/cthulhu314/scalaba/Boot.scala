package com.github.cthulhu314.scalaba

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import com.github.cthulhu314.scalaba.actors.{FileRepositoryActor, RepositoryActor}
import com.github.cthulhu314.scalaba.persistance.{InMemoryRepository, PerformanceLoggingRepositoryDecorator, MongoRepository, SlickRepository}
import akka.routing.RoundRobinRouter
import com.github.cthulhu314.scalaba.persistance.files.NopFileRepository
import com.github.cthulhu314.scalaba.generators.{Static, Files, Api}

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("scalaba")
  implicit val executionContext = system.dispatcher
//  val repository = new PerformanceLoggingRepositoryDecorator(new MongoRepository("","scalaba"))
  val repository = new PerformanceLoggingRepositoryDecorator(new InMemoryRepository())
  val dbActor = system.actorOf(Props(new RepositoryActor(repository))
    .withRouter(RoundRobinRouter(1)))
  val fileActor = system.actorOf(Props(new FileRepositoryActor(new NopFileRepository())))
  // create and start our service actor
  val service = system.actorOf(Props(new ScalabaGeneratorsActor(
    Seq(
      Api(dbActor),
      Files(fileActor),
      Static(system)
    ))), "scalaba-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
}