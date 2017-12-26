package io.bfil.rx.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import rx.lang.scala.Observer

object KafkaObserver {
  def apply[T](producer: KafkaProducer[String, Array[Byte]])(f: T => Unit) =
    Observer(f, ex => throw ex, () => producer.close)
}