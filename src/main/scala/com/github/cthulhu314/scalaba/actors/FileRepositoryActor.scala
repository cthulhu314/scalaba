package com.github.cthulhu314.scalaba.actors

import com.github.cthulhu314.scalaba.persistance.files.FileRepository
import akka.actor._
import java.io.File

case class CreateFile(file : Array[Byte])
case class DeleteFile(filename : String)
case class GetFile(filename : Array[Byte])

class FileRepositoryActor(files : FileRepository) extends Actor {
  def receive: Actor.Receive = {
    case CreateFile(name) => sender ! files.create(name)
    case DeleteFile(name) => files.delete(name)
  }
}
