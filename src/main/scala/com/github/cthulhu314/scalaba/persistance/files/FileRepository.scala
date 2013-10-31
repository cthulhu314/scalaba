package com.github.cthulhu314.scalaba.persistance.files

trait FileRepository {
  def create(file : Array[Byte]) : Option[String]
  def delete(name : String) : Boolean
}
