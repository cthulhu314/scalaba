package com.github.cthulhu314.scalaba

import akka.actor._
import akka.pattern._
import spray.routing._
import spray.routing.authentication._
import spray.routing.directives.SecurityDirectives
import spray.http._
import spray.routing.directives.CachingDirectives._
import com.github.cthulhu314.scalaba.auth._
import MediaTypes._
import spray.httpx.encoding._
import scala.concurrent.duration.{DurationDouble, Duration}
import spray.routing.directives.CacheSpecMagnet
import spray.httpx.Json4sJacksonSupport
import spray.httpx.marshalling._
import com.github.cthulhu314.scalaba.models.{Thread,Post}
import com.github.cthulhu314.scalaba.actors._
import org.json4s.{FieldSerializer, DefaultFormats, Formats}
import org.json4s
import scala.concurrent.ExecutionContext
import org.omg.CosNaming.NamingContextPackage.NotFound


class ScalabaActor(dbActor : ActorRef, filesActor : ActorRef)
                  (implicit val executionContext : ExecutionContext) extends Actor with Json4sJacksonSupport with HttpService  {

  def actorRefFactory = context

  val simpleCache  = routeCache(maxCapacity = 1000, timeToIdle = Duration("30 min"))

  implicit val json4sJacksonFormats : Formats = DefaultFormats + json4s.FieldSerializer[Thread]() + FieldSerializer[Post]()
  implicit val timeout = akka.util.Timeout(1000)

  val myRoute =
    pathPrefix("api") {
      path("threads") {
        get {
          parameters('from.as[Int] ? 0,'count.as[Int] ? 10) { case (from,count) =>
            produce(instanceOf[Seq[Thread]]) { cpl => _ =>
              (dbActor ? GetThreads(from,count)).mapTo[Seq[Thread]].foreach(cpl)
            }
          }
        }
      } ~
      pathPrefix("thread" / IntNumber) { id =>
        get {
          produce(instanceOf[Option[Thread]]) { cpl => _ =>
            (dbActor ? GetThread(id)).mapTo[Option[Thread]].foreach(cpl)
          }
        }
      } ~
      path("thread") {
        post {
          entity(as[Thread]) { thread =>
            complete {
              (dbActor ? CreateThread(thread)).map {
                case Some(_) => StatusCodes.OK
                case None => StatusCodes.NotFound
              }
            }
          }
        }

      } ~
      pathPrefix("post" / IntNumber) { id =>
        get {
          produce(instanceOf[Option[Post]]) { cpl => _ =>
            (dbActor ? GetPost(id)).mapTo[Option[Post]].foreach(cpl)
          }
        }
      } ~
      path("post") {
        post  {
          entity(as[Post]) { post =>
            complete {
              (dbActor ? CreatePost(post)).map {
                case Some(_) => StatusCodes.OK
                case None => StatusCodes.NotFound
              }
            }
          }
        }
      }
    } ~
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
    } ~
    get {
     // cache(simpleCache) {
        getFromResourceDirectory("static")
     // }
    } ~
    get {
      getFromFile("index.html")
    }


  def receive = runRoute(myRoute)
}
