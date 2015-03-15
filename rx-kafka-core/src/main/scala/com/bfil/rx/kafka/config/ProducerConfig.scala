package com.bfil.rx.kafka.config

import com.typesafe.config.Config

import kafka.producer.{ProducerConfig => KafkaProducerConfig}

object ProducerConfig {
  def apply(config: Config): KafkaProducerConfig = {

    val props = new PropertiesBuilder(config).build(Map(
      "serializer.class" -> ("serializer.class", ConfigTypes.String),
      "key.serializer.class" -> ("key.serializer.class", ConfigTypes.String),
      "metadata.broker.list" -> ("metadata.broker.list", ConfigTypes.String),
      "request.timeout.ms" -> ("request.timeout", ConfigTypes.FiniteDuration),
      "request.required.acks" -> ("request.required.acks", ConfigTypes.Int),
      "producer.type" -> ("producer.type", ConfigTypes.String),
      "partitioner.class" -> ("partitioner.class", ConfigTypes.String),
      "compression.codec" -> ("compression.codec", ConfigTypes.String),
      "compressed.topics" -> ("compressed.topics", ConfigTypes.String),
      "message.send.max.retries" -> ("message.send.max.retries", ConfigTypes.Int),
      "retry.backoff.ms" -> ("retry.backoff", ConfigTypes.FiniteDuration),
      "topic.metadata.refresh.interval.ms" -> ("topic.metadata.refresh.interval", ConfigTypes.FiniteDuration),
      "queue.buffering.max.ms" -> ("queue.buffering.max", ConfigTypes.FiniteDuration),
      "queue.buffering.max.messages" -> ("queue.buffering.max-messages", ConfigTypes.Int),
      "queue.enqueue.timeout.ms" -> ("queue.enqueue.timeout", ConfigTypes.Duration),
      "batch.num.messages" -> ("batch.num.messages", ConfigTypes.Int),
      "send.buffer.bytes" -> ("send.buffer", ConfigTypes.Bytes),
      "client.id" -> ("client.id", ConfigTypes.String)))

    new KafkaProducerConfig(props)
  }
}