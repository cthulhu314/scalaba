package com.github.cthulhu314.scalaba.actors

import akka.actor._
import com.github.cthulhu314.scalaba.captcha.Captcha

case class Generate()
case class Validate(key : String,result : String)

class CaptchaActor(captcha : Captcha) extends Actor {
  def receive: Actor.Receive = {
    case Generate() => sender ! captcha.generate()
    case Validate(key,result) => sender ! captcha.validate(key,result)
  }
}
