package com.github.cthulhu314.scalaba.authorization

import scala.concurrent.{ ExecutionContext, Future }
import shapeless.HNil
import spray.routing.authentication._
import spray.routing._
import spray.routing.directives.BasicDirectives._
import spray.routing.RequestContext
import spray.routing.directives.RouteDirectives._
import spray.routing.RequestContext
import spray.routing.directives.MiscDirectives._
import spray.routing.RequestContext
import spray.routing.directives.{RouteDirectives, MiscDirectives, FutureDirectives, BasicDirectives}
import BasicDirectives._
import FutureDirectives._
import MiscDirectives._
import RouteDirectives._
import spray.routing.directives.OnSuccessFutureMagnet


object FutureAuthorizeDirective {
  def authorize(check: RequestContext ⇒ Future[Boolean])(implicit context : ExecutionContext): Directive0 =
    extract(check).flatMap(onSuccess(_)).flatMap[HNil](if (_) pass else reject(AuthorizationFailedRejection)) &
      cancelRejection(AuthorizationFailedRejection)
}
