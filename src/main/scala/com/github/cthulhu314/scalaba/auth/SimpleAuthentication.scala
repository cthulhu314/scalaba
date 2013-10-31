package com.github.cthulhu314.scalaba.auth

class SimpleAuthentication(password : String) extends Authentication[String,Boolean] {
	override def authenticate(userContext : String) = Stream.cons(userContext == password,Stream.empty)
}