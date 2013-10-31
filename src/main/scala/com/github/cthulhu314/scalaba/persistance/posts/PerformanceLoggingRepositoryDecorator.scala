package com.github.cthulhu314.scalaba.persistance

import com.github.cthulhu314.scalaba.models.{Thread, Post}
import com.typesafe.scalalogging.slf4j.Logging


class PerformanceLoggingRepositoryDecorator(decorated : Repository) extends Repository with Logging {
  private def timeOf[K,T](name : String)(action : K => T )(k : K) : T = {
    val now = System.nanoTime
    val result = action(k)
    val micros = (System.nanoTime - now) / 1000
    logger.info("Method " + name + " in " + decorated.getClass.getName + " took " + micros + " microseconds")
    result
  }
  private def timeOf2[K,T,L](name : String)(action : (K, T) => L)(k : K, t : T) : L = {
    val now = System.nanoTime
    val result = action(k,t)
    val micros = (System.nanoTime - now) / 1000
    logger.info("Method " + name + " in " + decorated.getClass.getName + " took " + micros + " microseconds")
    result
  }

  def getPost(id: Int): Option[Post] = timeOf("getPost")(decorated.getPost)(id)

  def getThread(id: Int): Option[Thread] = timeOf("getThread")(decorated.getThread)(id)

  def getThreads(from : Int, count : Int) : Seq[Thread] = timeOf2("getThreads")(decorated.getThreads)(from,count)

  def create(thread: Thread): Option[Thread] = timeOf[Thread,Option[Thread]]("create[Thread]")(decorated.create)(thread)

  def create(post: Post): Option[Post] = timeOf[Post,Option[Post]]("create[Post]")(decorated.create)(post)

  def delete(thread: Thread): Unit =  timeOf[Thread,Unit]("delete[Thread]")(decorated.delete)(thread)

  def delete(post: Post): Unit = timeOf[Post,Unit]("delete[Post")(decorated.delete)(post)


}
