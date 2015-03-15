package com.bfil.rx.kafka.producer

import com.bfil.rx.kafka.messaging.Topic

import rx.lang.scala.Observer

class KafkaTopicProducer[T](topic: Topic[T], protected val config: kafka.producer.ProducerConfig) extends AbstractKafkaProducer {
  def publish(message: T) = super.publish(topic, message)
  def toObserver: Observer[T] = super.toObserver(t => publish(t))
}