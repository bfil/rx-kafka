package io.bfil.rx.kafka.messaging

import org.json4s.Formats

import io.bfil.rx.kafka.serialization.{Json4sDeserializer, Json4sSerializer}

abstract class JsonTopic[T <: AnyRef : Manifest](val name: String)(implicit formats: Formats) extends Topic[T] {
  val messageManifest = manifest[T]
  val serializer = new Json4sSerializer[T]
  val deserializer = new Json4sDeserializer[T]
}