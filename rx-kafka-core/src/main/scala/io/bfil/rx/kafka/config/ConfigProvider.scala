package io.bfil.rx.kafka.config

import com.typesafe.config.ConfigFactory

trait ConfigProvider {
  protected val config = ConfigFactory.load
  protected lazy val defaultProducerConfig = config.getConfig("kafka.producer")
  protected lazy val defaultConsumerConfig = config.getConfig("kafka.consumer")
}