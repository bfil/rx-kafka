package com.bfil.rx.kafka.messaging

import com.bfil.rx.kafka.serialization.{Deserializer, Serializer}

trait Topic[T] {
  val name: String
  type Message = T
  val serializer: Serializer[T]
  val deserializer: Deserializer[T]
}