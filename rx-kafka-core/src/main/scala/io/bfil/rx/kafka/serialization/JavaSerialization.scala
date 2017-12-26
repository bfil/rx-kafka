package io.bfil.rx.kafka.serialization

import java.io.{ByteArrayInputStream, ObjectInputStream, ObjectOutputStream}

import org.apache.commons.io.output.ByteArrayOutputStream

class JavaSerializer extends Serializer[Any] {
  def toBytes(obj: Any): Array[Byte] = {
    val bos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(bos)
    out.writeObject(obj)
    out.close()
    bos.toByteArray
  }
}

class JavaDeserializer[T <: Any] extends Deserializer[T] {
  def fromBytes(bytes: Array[Byte]): T = {
    val bais = new ByteArrayInputStream(bytes)
    val ois = new ObjectInputStream(bais)
    val result = ois.readObject
    result.asInstanceOf[T]
  }
}