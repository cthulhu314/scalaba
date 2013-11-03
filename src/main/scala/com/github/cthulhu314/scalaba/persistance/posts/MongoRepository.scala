package com.github.cthulhu314.scalaba.persistance.posts

import com.github.cthulhu314.scalaba.models.{Thread, Post}
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import scala.Some
import java.sql.Timestamp

class MongoRepository(url : String, database : String) extends Repository{
  val db = MongoClient(url)
  val posts = db(database)("posts")
  val idColl = db(database)("id_coll")

  if(idColl.count() == 0) {
    idColl.insert(MongoDBObject("id" -> 0))
  }

  posts.ensureIndex("id")
  posts.ensureIndex("posts.id")

  private def withId[T](action : Int => T) : T = {
    idColl.findAndModify(MongoDBObject(),$inc(("id",1)))
    action(idColl.head.get("id").asInstanceOf[Int])
  }

  def getPost(id: Int): Option[Post] = {
    posts.findOne("posts" $elemMatch MongoDBObject("id" -> id))
      .map(_.asInstanceOf[BasicDBObject])
      .flatMap(p => grater[Thread].asObject(new MongoDBObject(p)).posts.headOption)
  }

  def getThread(id: Int): Option[Thread] = {
    posts.findOne(MongoDBObject("id" -> id))
      .map(_.asInstanceOf[BasicDBObject])
      .map(p => grater[Thread].asObject(new MongoDBObject(p)))
  }

  def getThreads(from: Int, count: Int): Seq[Thread] = {
    posts.find().$orderby(MongoDBObject("date" -> 1)).skip(from).limit(count)
      .map(_.asInstanceOf[BasicDBObject])
      .map(p => grater[Thread].asObject(new MongoDBObject(p)))
      .toSeq
  }

  def create(thread: Thread): Option[Thread] = {
    withId { id =>
      val date = new Timestamp(System.currentTimeMillis())
      val newThread = thread.copy(
        id = id,
        posts = thread.posts.map{p => p.copy(id=Some(id),threadId = Some(id), date = Some(date))}.take(1))
      posts.insert(grater[Thread].asDBObject(newThread))
      Some(newThread)
    }
  }

  def create(post: Post): Option[Post] = {
    posts.findOne(MongoDBObject("id" -> post.threadId)).map {_ =>
      withId { id =>
        val date = new Timestamp(System.currentTimeMillis())
        val newPost = post.copy(id = Some(id), date = Some(date))
        posts.findAndModify(MongoDBObject("id" -> post.threadId),
          $push(("posts",grater[Post].asDBObject(newPost))) ++ $set(("date",post.date)))
        newPost
      }
    }
  }

  def delete(thread: Thread): Unit = {
    posts.findAndRemove(MongoDBObject("id" -> thread.id))
  }

  def delete(post: Post): Unit = {
    posts.findAndRemove(MongoDBObject("id" -> post.id))
  }
}
