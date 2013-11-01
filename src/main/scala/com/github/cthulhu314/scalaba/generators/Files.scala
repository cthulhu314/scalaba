package com.github.cthulhu314.scalaba.generators

import spray.routing.Directives
import akka.actor._
import akka.pattern._
import scala.concurrent.ExecutionContext
import spray.httpx.unmarshalling.Deserializer
import com.github.cthulhu314.scalaba.actors.CreateFile
import scala.concurrent.duration.{DurationDouble, Duration}
import Deserializer._

object Files extends Directives {
  def apply(filesActor : ActorRef)
           (implicit context : ExecutionContext, actorRefFactory : ActorRefFactory) = {
    path("files") {
      post {
        detachTo(singleRequestServiceActor) {
          formField('file.as[Array[Byte]]) { file =>
            produce(instanceOf[Option[String]]) { cpl => _ =>
              (filesActor ? CreateFile(file)).mapTo[Option[String]].foreach(cpl)
            }
          }
        }
      } ~
        get {
          getFromResourceDirectory("files")
        }
    }
  }
  implicit val timeout : akka.util.Timeout = 5.seconds
}
