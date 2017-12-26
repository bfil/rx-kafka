package io.bfil.rx.kafka.messaging

import io.bfil.rx.kafka.serialization.{Deserializer, Serializer}

trait Topic[T] {
  val name: String
  type Message = T
  val serializer: Serializer[T]
  val deserializer: Deserializer[T]
}