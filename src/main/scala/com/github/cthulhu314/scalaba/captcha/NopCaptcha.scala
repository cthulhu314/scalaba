package com.github.cthulhu314.scalaba.captcha

class NopCaptcha extends Captcha {
	def generate() = {
		CaptchaImage("",Array.empty)
	}

	def validate(id : String,result : String) = {
		false	
	}
}