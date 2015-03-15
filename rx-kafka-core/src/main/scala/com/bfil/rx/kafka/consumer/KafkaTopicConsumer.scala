package com.bfil.rx.kafka.consumer

import com.bfil.rx.kafka.KafkaObservable
import com.bfil.rx.kafka.messaging.Topic

class KafkaTopicConsumer[T](topic: Topic[T], protected val config: kafka.consumer.ConsumerConfig) extends AbstractKafkaConsumer[T] {
  private val stream = connector.createMessageStreams(Map(topic.name -> 1)).get(topic.name).get(0)
  val iterator = stream.iterator.map(m => topic.deserializer.fromBytes(m.message))
  def toObservable() = KafkaObservable(iterator)
}