package com.bfil.rx.kafka.producer

import com.bfil.rx.kafka.KafkaObserver
import com.bfil.rx.kafka.messaging.Topic

import kafka.producer.{KeyedMessage, Producer}

abstract class AbstractKafkaProducer {
  protected val config: kafka.producer.ProducerConfig
  protected val producer = new Producer[String, Array[Byte]](config)
  protected def publish[T](topic: Topic[T], message: T) = producer.send(new KeyedMessage[String, Array[Byte]](topic.name, topic.serializer.toBytes(message)))
  protected def toObserver[T](f: T => Unit) = KafkaObserver(producer)(f)
  def close = producer.close
}