package com.github.cthulhu314.scalaba

import akka.actor.{Actor, ActorRef}
import scala.concurrent.ExecutionContext
import spray.httpx.Json4sJacksonSupport
import spray.routing.HttpService
import spray.routing.directives.CachingDirectives._
import scala.concurrent.duration.Duration
import org.json4s.{FieldSerializer, DefaultFormats, Formats}
import org.json4s
import com.github.cthulhu314.scalaba.models.{Post, Thread}
import spray.routing._


class ScalabaGeneratorsActor(routes : Iterable[Route])
                            (implicit val executionContext : ExecutionContext) extends Actor with HttpService {

  def actorRefFactory = context

  val simpleCache  = routeCache(maxCapacity = 1000, timeToIdle = Duration("30 min"))
  implicit val timeout = akka.util.Timeout(5000)

  def receive: Actor.Receive = runRoute(routes.reduce {_ ~ _})
}
