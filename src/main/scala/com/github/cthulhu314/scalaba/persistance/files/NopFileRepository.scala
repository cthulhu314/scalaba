package com.github.cthulhu314.scalaba.persistance.files

class NopFileRepository extends FileRepository {
  def create(file: Array[Byte]): Option[String] = {
    None
  }

  def delete(name: String): Boolean = {
  	false
  }
}
