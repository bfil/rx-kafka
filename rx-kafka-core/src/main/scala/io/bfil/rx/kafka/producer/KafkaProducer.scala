package io.bfil.rx.kafka.producer

import io.bfil.rx.kafka.config.{ConfigProvider, ProducerConfig}
import io.bfil.rx.kafka.messaging.Topic
import com.typesafe.config.Config

import rx.lang.scala.Observer

class KafkaProducer(protected val config: java.util.Properties) extends AbstractKafkaProducer {
  override def publish[T](topic: Topic[T], message: T) = super.publish(topic, message)
  def toObserver[T]: Observer[(Topic[T], T)] = super.toObserver({ case (topic, message) => publish(topic, message) })
}

object KafkaProducer extends ConfigProvider {
  def apply(config: Config = defaultProducerConfig) = new KafkaProducer(ProducerConfig(config))
  def apply[T](topic: Topic[T]) = new KafkaTopicProducer(topic, ProducerConfig(defaultProducerConfig))
  def apply[T](topic: Topic[T], config: Config) = new KafkaTopicProducer(topic, ProducerConfig(config))
}