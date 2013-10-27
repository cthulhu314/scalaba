package com.github.cthulhu314.scalaba.models

case class Post(id : Option[Int],
                threadId : Option[Int],
                title : Option[String],
                author: Option[String],
                image : Option[String],
                text : String,
                date : Option[java.sql.Timestamp])

case class Thread(id : Int, posts : Seq[Post])
