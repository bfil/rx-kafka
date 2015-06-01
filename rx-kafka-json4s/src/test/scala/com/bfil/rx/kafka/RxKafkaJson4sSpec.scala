package com.bfil.rx.kafka

import scala.concurrent.Future

import org.apache.log4j.Level
import org.json4s.DefaultFormats

import com.bfil.kafka.specs2.EmbeddedKafkaContext
import com.bfil.rx.kafka.consumer.KafkaConsumer
import com.bfil.rx.kafka.messaging.JsonTopic
import com.bfil.rx.kafka.producer.KafkaProducer
import com.bfil.rx.kafka.serialization.JsonSupport
import com.bfil.rx.kafka.testkit.KafkaSpec

import rx.lang.scala.Observable

case class Test(value: String)
case class AnotherTest(count: Int)

class RxKafkaJson4sSpec extends KafkaSpec with JsonSupport {

  log.setLevel(Level.INFO)
  
  case object JsonTestTopic extends JsonTopic[Test]("test")
  case object AnotherJsonTestTopic extends JsonTopic[AnotherTest]("another-test")
  
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

      log.info("JSON Producer -> JSON Consumer")

      implicit val json4sFormats = DefaultFormats

      val consumer = KafkaConsumer(JsonTestTopic)
      val producer = KafkaProducer(JsonTestTopic)

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

      log.info("JSON Rx Producer -> JSON Rx Consumer")

      implicit val json4sFormats = DefaultFormats
      
      val consumer = KafkaConsumer(JsonTestTopic)

      val observable = consumer.toObservable
      val observer = KafkaProducer(JsonTestTopic).toObserver

      sendMessages { i =>
        observer.onNext(Test(i))
      }

      val receivedMessages = receiveMessages(observable)

      consumer.close
      observer.onCompleted

      receivedMessages.length === numMessages
      receivedMessages.map(_.value) === messages
    }
  }

  "A consumer of multiple topics" should {

    "consume all messages published by a producer" in new WithEmbeddedKafkaTopics {

      log.info("Multiple JSON Topics Producer -> Multiple JSON Topics Consumer")

      implicit val json4sFormats = DefaultFormats

      val consumer = KafkaConsumer(List(JsonTestTopic, AnotherJsonTestTopic))
      val producer = KafkaProducer()

      sendMessages { i =>
        if (i.toInt % 2 == 0) producer.publish(JsonTestTopic, Test(i))
        else producer.publish(AnotherJsonTestTopic, AnotherTest(i.toInt))
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