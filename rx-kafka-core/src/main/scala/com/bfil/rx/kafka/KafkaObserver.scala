package com.bfil.rx.kafka

import kafka.producer.Producer
import rx.lang.scala.Observer

object KafkaObserver {
  def apply[T](producer: Producer[String, Array[Byte]])(f: T => Unit) =
    Observer(f, ex => throw ex, () => producer.close)
}