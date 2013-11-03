package com.github.cthulhu314.scalaba.persistance.posts

import com.github.cthulhu314.scalaba.persistance.InMemoryRepository
import com.github.cthulhu314.scalaba.models.{Thread,Post}
import java.sql.Timestamp

class CachingRepository extends InMemoryRepository{

  override def create(thread : Thread) : Option[Thread] = {
    threadMap.put(thread.id,thread)
    thread.posts.foreach { post => postMap.put(post.id.get,post) }
    Some(thread)
  }

  override def create(post : Post) : Option[Post] = {
    post.threadId.flatMap(threadMap.get).map { thread =>
      threadMap.update(post.threadId.get,thread.copy(posts = thread.posts ++ Seq(post)))
      postMap.put(post.id.get,post)
      post
    }
  }
}
