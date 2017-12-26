package io.bfil.rx.kafka.producer

import io.bfil.rx.kafka.KafkaObserver
import io.bfil.rx.kafka.messaging.Topic

import org.apache.kafka.clients.producer.ProducerRecord

abstract class AbstractKafkaProducer {
  protected val config: java.util.Properties
  protected val producer = new org.apache.kafka.clients.producer.KafkaProducer[String, Array[Byte]](config)
  protected def publish[T](topic: Topic[T], message: T) = producer.send(new ProducerRecord[String, Array[Byte]](topic.name, topic.serializer.toBytes(message)))
  protected def toObserver[T](f: T => Unit) = KafkaObserver(producer)(f)
  def close = producer.close
}