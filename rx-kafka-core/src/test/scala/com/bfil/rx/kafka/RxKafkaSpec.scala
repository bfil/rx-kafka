package com.bfil.rx.kafka

import scala.concurrent.Future

import org.apache.log4j.Level

import com.bfil.kafka.specs2.EmbeddedKafkaContext
import com.bfil.rx.kafka.consumer.KafkaConsumer
import com.bfil.rx.kafka.messaging.SerializableTopic
import com.bfil.rx.kafka.producer.KafkaProducer
import com.bfil.rx.kafka.testkit.KafkaSpec

import rx.lang.scala.Observable

case class Test(value: String)
case class AnotherTest(count: Int)

class RxKafkaSpec extends KafkaSpec {

  log.setLevel(Level.INFO)
  
  case object TestTopic extends SerializableTopic[Test]("test")
  case object AnotherTestTopic extends SerializableTopic[AnotherTest]("another-test")
  
  trait WithEmbeddedKafkaTopics extends EmbeddedKafkaContext {
    val topics = Set("test", "another-test")
  }

  val numMessages = 10
  val messages = (1 to numMessages).map(_.toString).toList  

  def sendMessages(f: String => Unit) = Future {
    Thread.sleep(200)
    for (i <- messages) f(i)
  }

  def receiveMessages[T](iterator: Iterator[T]): List[T] = receiveMessages(iterator, numMessages)
  def receiveMessages[T](observable: Observable[T]): List[T] = receiveMessages(observable, numMessages)

  sequential

  "A consumer" should {

    "consume all messages published by a producer" in new WithEmbeddedKafkaTopics {

      log.info("Producer -> Consumer")

      val consumer = KafkaConsumer(TestTopic)
      val producer = KafkaProducer(TestTopic)

      sendMessages { i =>
        producer.publish(Test(i))
      }

      val receivedMessages = receiveMessages(consumer.iterator)

      consumer.close
      producer.close

      receivedMessages.length === numMessages
      receivedMessages.map(_.value) === messages
    }
  }

  "An rx observable" should {

    "consume all messages published by an rx observer" in new WithEmbeddedKafkaTopics {

      log.info("Rx Producer -> Rx Consumer")

      val observable = KafkaConsumer(TestTopic).toObservable
      val observer = KafkaProducer(TestTopic).toObserver

      sendMessages { i =>
        observer.onNext(Test(i))
      }

      val receivedMessages = receiveMessages(observable)

      observer.onCompleted

      receivedMessages.length === numMessages
      receivedMessages.map(_.value) === messages
    }
  }

  "A consumer of multiple topics" should {

    "consume all messages published by a producer" in new WithEmbeddedKafkaTopics {

      log.info("Multiple Topics Producer -> Multiple Topics Consumer")

      val consumer = KafkaConsumer(List(TestTopic, AnotherTestTopic))
      val producer = KafkaProducer()

      sendMessages { i =>
        if (i.toInt % 2 == 0) producer.publish(TestTopic, Test(i))
        else producer.publish(AnotherTestTopic, AnotherTest(i.toInt))
      }

      val receivedMessages = receiveMessages(consumer.toObservable)

      consumer.close
      producer.close

      receivedMessages.length === numMessages
      receivedMessages.map {
        case Test(value)        => value
        case AnotherTest(count) => count.toString
      }.toSet === messages.toSet
    }
  }
}