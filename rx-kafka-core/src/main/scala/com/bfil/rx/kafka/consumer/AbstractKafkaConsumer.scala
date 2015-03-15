package com.bfil.rx.kafka.consumer

import kafka.consumer.Consumer
import rx.lang.scala.Observable

abstract class AbstractKafkaConsumer[T] {
  protected val config: kafka.consumer.ConsumerConfig
  protected val connector = Consumer.create(config)
  def toObservable(): Observable[T]
  def subscribe(f: T => Unit) = toObservable.subscribe(f)
  def close = connector.shutdown
}