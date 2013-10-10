package com.github.cthulhu314.scalaba

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._


class ScalabaActor extends Actor with Scalaba {


  def actorRefFactory = context


  def receive = runRoute(myRoute)
}


trait Scalaba extends HttpService {

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { 
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }
}