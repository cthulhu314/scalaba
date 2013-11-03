package com.github.cthulhu314.scalaba.persistance.files

import java.io._
import java.util.concurrent.atomic.AtomicInteger

class SimpleFileRepository(path : String) extends FileRepository {
	private val id = new AtomicInteger(0)
	def create(file : Array[Byte]) : Option[String] = {
		val fos = new FileOutputStream(path + "/" + id)
		var result : Option[String] = None
		try {
			fos.write(file)
			result = Some(id.getAndIncrement.toString)
		}
		finally {
			fos.close()
		}
		result
	}
  def delete(name : String) : Boolean = {
    var result = false
    try {
      val file = new File(name)
      result = file.delete()
    } catch {
      case _ : Throwable => { }
    }
    result
  }
}