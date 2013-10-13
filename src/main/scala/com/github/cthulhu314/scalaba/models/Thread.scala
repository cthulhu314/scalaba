package com.github.cthulhu314.scalaba.models

case class Post(id : Option[Int], threadId : Option[Int], title : String, author: String, image : String,text : String, date : java.sql.Date)

case class Thread(id : Int, posts : Seq[Post])
