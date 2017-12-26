package io.bfil.rx.kafka

import rx.lang.scala.Observable
import rx.lang.scala.schedulers.NewThreadScheduler

object KafkaObservable {
  def apply[T](iterator: Iterator[T]): Observable[T] =
    Observable[T] { subscriber =>
      NewThreadScheduler().createWorker.scheduleRec {
        if (iterator.hasNext && !subscriber.isUnsubscribed) subscriber.onNext(iterator.next)
        else subscriber.onCompleted
      }
    }
}