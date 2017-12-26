package io.bfil.rx.kafka.messaging

import io.bfil.rx.kafka.serialization.{JavaDeserializer, JavaSerializer}

abstract class SerializableTopic[T](val name: String) extends Topic[T] {
  val serializer = new JavaSerializer
  val deserializer = new JavaDeserializer
}