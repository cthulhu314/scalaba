package com.github.cthulhu314.scalaba.persistance

import com.github.cthulhu314.scalaba.models.{Thread, Post}
import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.ColumnBase
import java.sql.{Timestamp, Date}
import java.lang
import scala.slick.jdbc.meta.MTable


class SlickRepository extends Repository {

  object Posts extends Table[Post]("posts") {
    def id = column[Int]("id",O.PrimaryKey,O.AutoInc)
    def threadId = column[Int]("thread_id")
    def title = column[String]("title")
    def author = column[String]("author")
    def image = column[String]("image")
    def text = column[String]("text")
    def date = column[Timestamp]("date")

    def * : ColumnBase[Post] = (id.? ~ threadId.? ~ title.? ~ author.? ~ image.? ~ text ~ date.?) <> (Post.apply _,Post.unapply _)
    def forInsert = (threadId.? ~ title.? ~ author.? ~ image.? ~ text ~ date.?) <> ({t => Post(None,t._1,t._2,t._3,t._4,t._5,t._6)},
      {(p : Post) => Some((p.threadId,p.title,p.author,p.image,p.text,p.date)) })
  }
  val db = Database.forURL( "jdbc:sqlserver://localhost;databaseName=cust;instanceName=SQLEXPRESS;", driver = "org.h2.Driver")
  db.withSession { implicit session : Session =>
    if(!MTable.getTables.list.exists(_.name.name == Posts.tableName))  {
      Posts.ddl.create(session)
    }

  }
  private def getThreadFromPostsAndId(id : Int, posts : Seq[Post] ) = {
    if(posts.isEmpty)
      None
    else
      Some(Thread(id,posts))
  }

  def getPost(id: Int): Option[Post] = {
    db.withSession { implicit session =>
      Posts.filter(_.id === id).map(_.*).list.headOption
    }
  }

  def getThread(id: Int): Option[Thread] = {
    db.withSession { implicit session =>
      getThreadFromPostsAndId(id,Posts.filter(_.threadId === id).sortBy(_.date).map(_.*).list)
    }

  }

  def getThreads(from: Int, count: Int): Seq[Thread] = {
    db.withSession { implicit session =>
      (for(
        threadId <- Posts.groupBy(_.threadId).map(x => (x._1,x._2.map(_.date).max)).sortBy(_._2).map(_._1).drop(from).take(count);
        post <- Posts.sortBy(_.date).filter(_.threadId === threadId)
      ) yield post).list.groupBy(_.threadId).map { p =>
        p._1.map(Thread.apply(_,p._2))
      }.filter(_.isDefined).map(_.get).toSeq
    }
  }

  def create(thread: Thread) : Option[Thread] = {
    db.withTransaction { implicit session =>
      thread.posts.headOption.map { post =>
        val postId = Posts.forInsert returning Posts.id insert post
        Posts.filter(_.id === postId).map(_.threadId).update(postId)
        Thread(postId,Seq(post.copy(id=Some(postId),threadId=Some(postId))))
      }
    }
  }

  def create(post: Post) : Option[Post] =  {
    db.withTransaction { implicit session =>
      post.threadId.flatMap {id =>
        if(Posts.filter(_.threadId === id).map(_.*).exists.run)
        {
          val postId = Posts.forInsert returning Posts.id insert post
          Some(post.copy(id = Some(postId)))
        } else None
      }

    }
  }

  def delete(thread: Thread) : Unit = {
    db.withTransaction { implicit session : Session =>
      Posts.filter(_.threadId === thread.id).delete
    }
  }

  def delete(post: Post) : Unit = {
    db.withTransaction { implicit session : Session =>
      Posts.filter(_.id === post.id).delete
    }
  }


}
