package com.github.cthulhu314.scalaba.persistance.boards

import com.github.cthulhu314.scalaba.persistance.posts.Repository

class SingleBoard(repository : Repository) extends Boards {
  def get(key: String): Option[Repository] = Some(repository)

  def iterator: Iterator[(String, Repository)] = Seq(("",repository)).iterator

  def -(key: String): Map[String, Repository] = this

  def +[B1 >: Repository](kv: (String, B1)): Map[String, B1] = this
}
