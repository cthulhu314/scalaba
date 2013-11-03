package com.github.cthulhu314.scalaba.persistance.posts

import com.github.cthulhu314.scalaba.models.{Thread,Post}

trait Repository {
  def getPost(id : Int) : Option[Post]
  def getThread(id : Int) : Option[Thread]
  def getThreads(from : Int, count : Int) : Seq[Thread]
  def create(thread : Thread) : Option[Thread]
  def create(post : Post)  : Option[Post]
  def delete(thread : Thread)
  def delete(post : Post)
}
