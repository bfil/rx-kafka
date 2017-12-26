package io.bfil.rx.kafka.producer

import io.bfil.rx.kafka.messaging.Topic

import rx.lang.scala.Observer

class KafkaTopicProducer[T](topic: Topic[T], protected val config: java.util.Properties) extends AbstractKafkaProducer {
  def publish(message: T) = super.publish(topic, message)
  def toObserver: Observer[T] = super.toObserver(t => publish(t))
}