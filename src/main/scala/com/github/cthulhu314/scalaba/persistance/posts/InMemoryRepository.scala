package com.github.cthulhu314.scalaba.persistance

import com.github.cthulhu314.scalaba.models.{Thread, Post}
import scala.collection.parallel.mutable._
import scala.collection._
import java.util.concurrent.atomic.AtomicInteger


class InMemoryRepository extends Repository{
  private val threadMap = new ParHashMap[Int,Thread]()
  private val postMap = new ParHashMap[Int,Post]()
  private val id = new AtomicInteger(0)

  private def withId[T](action : Int => T) = action(id.getAndIncrement)

  def getPost(id: Int): Option[Post] = postMap.get(id)

  def getThread(id: Int): Option[Thread] = threadMap.get(id)

  def getThreads(from: Int, count: Int): Seq[Thread] =
    threadMap.toStream.sortBy(_._2.posts.last.date.map(_.getTime).getOrElse[Long](0)).map(_._2).drop(from).take(count).toSeq

  def create(thread: Thread): Option[Thread] = {
    withId { id =>
      val newThread = thread.copy(id = id,posts = thread.posts.map{p => p.copy(id=Some(id),threadId = Some(id))})
      threadMap.put(id,newThread)
      postMap.put(id,thread.posts.head)
      Some(newThread)
    }
  }

  def create(post: Post): Option[Post] = {
    post.threadId.flatMap(threadMap.get).map { thread =>
      withId { id =>
        val newPost = post.copy(id = Some(id))
        threadMap.update(post.threadId.get,thread.copy(posts = thread.posts ++ Seq(newPost)))
        postMap.put(id,newPost)
        newPost
      }
    }
  }

  def delete(thread: Thread): Unit = {
    threadMap.remove(thread.id)
    for(post <- thread.posts)
      postMap.remove(post.id.get)
  }

  def delete(post: Post): Unit = {
    post.threadId.flatMap(threadMap.get).map { thread =>
      threadMap.update(post.threadId.get,thread.copy(posts = thread.posts.filter(_.id != post.id)))
      postMap.remove(post.id.get)
    }
  }
}
