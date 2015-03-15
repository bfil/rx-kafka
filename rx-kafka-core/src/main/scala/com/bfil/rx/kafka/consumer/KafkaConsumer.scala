package com.bfil.rx.kafka.consumer

import com.bfil.rx.kafka.KafkaObservable
import com.bfil.rx.kafka.config.{ConfigProvider, ConsumerConfig}
import com.bfil.rx.kafka.messaging.Topic
import com.typesafe.config.Config

import rx.lang.scala.Observable

class KafkaConsumer(topics: List[Topic[_]], protected val config: kafka.consumer.ConsumerConfig) extends AbstractKafkaConsumer[Any] {
  private val topicsMap: Map[String, Topic[_]] = topics.map(t => t.name -> t).toMap
  private val streams = connector.createMessageStreams(topics.map(_.name -> 1).toMap)
  val iterators = streams.map {
    case (topicName, stream) =>
      val topic = topicsMap.get(topicName).get
      val iterator = stream(0).iterator.map { m =>
        topic.deserializer.fromBytes(m.message)
      }
      (topicName, iterator)
  }
  def toObservable(): Observable[Any] = Observable.from(iterators.values.map(KafkaObservable.apply)).flatten
}

object KafkaConsumer extends ConfigProvider {
  def apply[T](topic: Topic[T], config: Config = defaultConsumerConfig) = new KafkaTopicConsumer(topic, ConsumerConfig(config))
  def apply(topics: List[Topic[_]]) = new KafkaConsumer(topics, ConsumerConfig(defaultConsumerConfig))
  def apply(topics: List[Topic[_]], config: Config) = new KafkaConsumer(topics, ConsumerConfig(config))
}