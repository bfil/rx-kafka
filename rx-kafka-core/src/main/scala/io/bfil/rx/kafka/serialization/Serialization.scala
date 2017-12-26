package io.bfil.rx.kafka.serialization

trait Serializer[-T] {
  def toBytes(obj: T): Array[Byte]
}

trait Deserializer[+T] {
  def fromBytes(bytes: Array[Byte]): T
}