package com.github.cthulhu314.scalaba.actors

import com.github.cthulhu314.scalaba.authentication.Authentication
import akka.actor._

case class Authenticate[T](context : T)

class AuthenticationActor[T,+K](auth : Authentication[T,K]) extends Actor {
  def receive = {
    case authenticate: Authenticate[T] => sender ! auth.authenticate(authenticate.context)
  }
}
