package io.bfil.rx.kafka.serialization

import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.Serialization

class Json4sSerializer[T <: AnyRef](implicit formats: Formats) extends Serializer[T] {
  def toBytes(obj: T): Array[Byte] = Serialization.write(obj).getBytes
}

class Json4sDeserializer[T: Manifest](implicit formats: Formats) extends Deserializer[T] {
  def fromBytes(bytes: Array[Byte]): T = Serialization.read[T](new String(bytes)) 
}

trait JsonSupport {
  implicit val json4sFormats = DefaultFormats.lossless
}