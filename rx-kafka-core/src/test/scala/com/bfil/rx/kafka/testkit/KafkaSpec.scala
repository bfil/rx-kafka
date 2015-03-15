package com.bfil.rx.kafka.testkit

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

import org.apache.log4j.Logger
import org.specs2.mutable.{BeforeAfter, Specification}
import org.specs2.time.NoTimeConversions

import rx.lang.scala.Observable

trait KafkaSpec extends Specification with NoTimeConversions {

  implicit val log = Logger.getRootLogger
  
  def logMessage[T](t: T): T = {
    log.info(t)
    t
  }
  
  val receiveTimeout = 2 seconds
  
  def waitForMessages[T](messages: => List[T]) = Await.result(Future(messages), receiveTimeout)
  
  def receiveMessages[T](iterator: Iterator[T], numMessages: Int): List[T] = 
    waitForMessages(iterator.map(logMessage).take(numMessages).toList)
  def receiveMessages[T](observable: Observable[T], numMessages: Int): List[T] = 
    waitForMessages(observable.map(logMessage).take(numMessages).toBlocking.toList)
  
}