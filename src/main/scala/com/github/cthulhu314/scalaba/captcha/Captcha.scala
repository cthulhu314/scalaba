package com.github.cthulhu314.scalaba.captcha

case class CaptchaImage(id : String, image : Array[Byte])

trait Captcha {
	def generate() : CaptchaImage
	def validate(id : String, result : String) : Boolean
}