package io.bfil.rx.kafka.config

import com.typesafe.config.Config

import kafka.consumer.{ConsumerConfig => KafkaConsumerConfig}

object ConsumerConfig {
  def apply(config: Config): KafkaConsumerConfig = {
    
    val props = new PropertiesBuilder(config).build(Map(
      "group.id" -> ("group.id", ConfigTypes.String),
      "zookeeper.connect" -> ("zookeeper.connect", ConfigTypes.String),
      "consumer.id" -> ("consumer.id", ConfigTypes.String),
      "socket.timeout.ms" -> ("socket.timeout", ConfigTypes.FiniteDuration),
      "socket.receive.buffer.bytes" -> ("socket.receive.buffer", ConfigTypes.Bytes),
      "fetch.message.max.bytes" -> ("fetch.message.max", ConfigTypes.Bytes),
      "num.consumer.fetchers" -> ("num.consumer.fetchers", ConfigTypes.Int),
      "auto.commit.enable" -> ("auto.commit.enable", ConfigTypes.Boolean),
      "auto.commit.interval.ms" -> ("auto.commit.interval", ConfigTypes.FiniteDuration),
      "queued.max.message.chunks" -> ("queued.max.message.chunks", ConfigTypes.Int),
      "rebalance.max.retries" -> ("rebalance.max.retries", ConfigTypes.Int),
      "fetch.min.bytes" -> ("fetch.min", ConfigTypes.Bytes),
      "fetch.wait.max.ms" -> ("fetch.wait.max", ConfigTypes.FiniteDuration),
      "rebalance.backoff.ms" -> ("rebalance.backoff", ConfigTypes.FiniteDuration),
      "refresh.leader.backoff.ms" -> ("refresh.leader.backoff", ConfigTypes.FiniteDuration),
      "auto.offset.reset" -> ("auto.offset.reset", ConfigTypes.String),
      "consumer.timeout.ms" -> ("consumer.timeout", ConfigTypes.Duration),
      "exclude.internal.topics" -> ("exclude.internal.topics", ConfigTypes.Boolean),
      "client.id" -> ("client.id", ConfigTypes.String),
      "zookeeper.session.timeout.ms" -> ("zookeeper.session.timeout", ConfigTypes.FiniteDuration),
      "zookeeper.connection.timeout.ms" -> ("zookeeper.connection.timeout", ConfigTypes.FiniteDuration),
      "zookeeper.sync.time.ms" -> ("zookeeper.sync.time", ConfigTypes.FiniteDuration),
      "offsets.storage" -> ("offsets.storage", ConfigTypes.String),
      "offsets.channel.backoff.ms" -> ("offsets.channel.backoff", ConfigTypes.FiniteDuration),
      "offsets.channel.socket.timeout.ms" -> ("offsets.channel.socket.timeout", ConfigTypes.FiniteDuration),
      "offsets.commit.max.retries" -> ("offsets.commit.max.retries", ConfigTypes.Int),
      "dual.commit.enabled" -> ("dual.commit.enabled", ConfigTypes.Boolean),
      "partition.assignment.strategy" -> ("partition.assignment.strategy", ConfigTypes.String)))

    new KafkaConsumerConfig(props)
  }
}