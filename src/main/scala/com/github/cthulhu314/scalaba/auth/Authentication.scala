package com.github.cthulhu314.scalaba.auth

trait Authentication[T,+User] {
	def authenticate(userContext : T) : Stream[User]
}
