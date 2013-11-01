package com.github.cthulhu314.scalaba.authentication

trait Authentication[T,+User] {
	def authenticate(userContext : T) : Stream[User]
}
