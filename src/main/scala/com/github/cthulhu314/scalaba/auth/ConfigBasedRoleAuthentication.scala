package com.github.cthulhu314.scalaba.auth

import spray.routing.RequestContext

trait Role
case class User() extends Role
case class Admin() extends Role

class ConfigBasedRoleAuthentication(users : Map[String,(String,Seq[Role])]) extends Authentication[Option[(String,String)],Role] {
  def authenticate(userContext: Option[(String, String)]): Stream[Role] = {
    userContext
      .flatMap { case (name,password) => users.get(name).filter(_._1 == password).map(_._2.toStream) }
      .getOrElse(Stream.empty)
  }
}
