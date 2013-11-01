package com.github.cthulhu314.scalaba.authentication

import spray.routing.RequestContext
import spray.routing.authentication.{UserPass, HttpAuthenticator}
import spray.http.{BasicHttpCredentials, HttpCredentials}
import scala.concurrent.{ExecutionContext, Future}
import spray.http.HttpHeaders._
import scala.reflect.{ classTag, ClassTag }

case class User(name : String, password : String, userAgent : String, ip : String)

class UserAuthentication(val realm : String)(implicit val executionContext: ExecutionContext) extends HttpAuthenticator[User]{
  def scheme: String = "By name, password, userAgent and ip"

  def params(ctx: RequestContext): Map[String, String] = Map.empty

  def authenticate(credentials: Option[HttpCredentials], ctx: RequestContext) = {
      Future {
        credentials.flatMap {
          case BasicHttpCredentials(user, pass) ⇒
            ctx.request.header[`X-Forwarded-For`].flatMap { forwarded =>
              ctx.request.header[`User-Agent`].flatMap { agent =>
                Some(User(user,pass,agent.value,forwarded.value))
              }
            }
          case _ ⇒ None
        }
      }
  }
}
