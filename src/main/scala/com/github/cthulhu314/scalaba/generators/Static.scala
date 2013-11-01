package com.github.cthulhu314.scalaba.generators

import spray.routing.Directives
import scala.concurrent.ExecutionContext
import akka.actor.ActorRefFactory

object Static extends Directives {
  def apply(implicit system : ActorRefFactory) = {
    get {
      // cache(simpleCache) {
      getFromResourceDirectory("static")
      // }
    } ~
    get {
      getFromFile("index.html")
    }
  }
}
