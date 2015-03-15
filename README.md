RxKafka
=======

This library provides a simple way to create Kafka consumers and producers, it uses [RxScala](https://github.com/ReactiveX/RxScala) under the hood, and provides the ability to turn consumers and producers into `RxScala`'s observables and observers.

Setting up the dependencies
---------------------------

__RxKafka__ is available at my [Nexus Repository](http://nexus.b-fil.com/nexus/content/groups/public/), and it is cross compiled and published for both Scala 2.10 and 2.11.

Using SBT, add the following dependency to your build file:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "rx-kafka-core" % "0.1.0"
)
```

Include the following module if you need JSON serialization (it uses [Json4s](https://github.com/json4s/json4s))

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "rx-kafka-json4s" % "0.1.0"
)
```

Don't forget to add the following resolver:

```scala
resolvers += "BFil Nexus Releases" at "http://nexus.b-fil.com/nexus/content/repositories/releases/"
```

### Using snapshots

If you need a snapshot dependency:

```scala
libraryDependencies ++= Seq(
  "com.bfil" %% "rx-kafka-core" % "0.2.0-SNAPSHOT"
)

resolvers += "BFil Nexus Snapshots" at "http://nexus.b-fil.com/nexus/content/repositories/snapshots/";
```

Usage
-----

### Topics

The topics in `RxKafka` are typed, the following is the `Topic` trait:

```scala
trait Topic[T] {
  val name: String
  type Message = T
  val serializer: Serializer[T]
  val deserializer: Deserializer[T]
}
```

Topics can be defined by extending the trait, each topic has its own name (used by Kafka), and the type of the message it contains (often needed by the serialization process).

The `core` module supports basic Java serialization, and exposes an abstract `SerializableTopic` class that can be easily extended to define new topics that will be serialized using Java serialization, as follows:

```scala
case object TestTopic extends SerializableTopic[Test]("test")
case object AnotherTestTopic extends SerializableTopic[AnotherTest]("another-test")
```

`Test` and `AnotherTest` are simple case classes representing the message.

The `json4s` module provides a similar class, called `JsonTopic`, that can be used as follows:

```scala
case object JsonTestTopic extends JsonTopic[Test]("test")
case object AnotherJsonTestTopic extends JsonTopic[AnotherTest]("another-test")
```

This topics can then be used to create consumers and producers, and the self contained serialization simplifies the rest of the APIs.

### Consumers

Two types of consumers can be created, consumers consuming a single topic, or multiple topics.

#### Single-Topic Consumer

To create and use an RxKafka consumer tied to a single topic:

```scala
val consumer = KafkaConsumer(TestTopic)

// an iterator can be accessed on the consumer
val iterator = consumer.iterator

// or the consumer can be turned into an Observable[T] where T is the type of the message (in this case it will be Test)
val observable = consumer.toObservable

// you can also call subscribe directly on the Consumer (it's just a proxy to the Observable's subscribe method)
consumer.subscribe { message =>
	// ...
}

// to close the Kafka connector
consumer.close
```

#### Multiple-Topics Consumer

To create and use an RxKafka consumer tied to multiple topics:

```scala
val consumer = KafkaConsumer(List(TestTopic, AnotherTestTopic))

// a map of iterators can be accessed on the consumer as a Map[String, Iterator[Any]]
val iterators = consumer.iterators

// or the consumer can be turned into an Observable[Any] which will merge all topics together:
val observable = consumer.toObservable

// you can also call subscribe directly on the `Consumer`
consumer.subscribe { 
  case test: Test => // ...
  case anotherTest: AnotherTest => // ...
}

// to close the Kafka connector
consumer.close
```

### Producers

Two types of producers can be created, producers producing a single topic, or multiple topics.

#### Single-Topic Producer

To create and use an RxKafka producer for a single topic:

```scala
val producer = KafkaProducer(TestTopic)

// plublish a message
producer.publish(Test("test"))

// or the producer can be turned into an Observer[T] where T is the type of the message (in this case it will be Test)
val observer = producer.toObserver

// then you can use the Observer API
observer.onNext(Test("test"))
observer.onComplete()

// stop the Kafka producer
producer.close
```

#### Multiple-Topics Producer

```scala
val producer = KafkaProducer()

// plublish a message to a particular topic
producer.publish(TestTopic, Test("test"))
producer.publish(AnotherTestTopic, AnotherTest("test"))

// or the producer can be turned into an Observer[(Topic[T], T)]
val observer = producer.toObserver

// then you can use the Observer API
observer.onNext(TestTopic, Test("test"))
observer.onNext(AnotherTestTopic, AnotherTest("test"))
observer.onComplete()

// stop the Kafka producer
producer.close
```

### Configuration

Configuration is supported using [Typesafe Config](https://github.com/typesafehub/config).

To customize default consumers and producers configuration create an `application.conf` file under your `src/main/resources` folder.

The following is an example configuration file:

```
kafka {
	consumer {
		group.id = "default"
		zookeeper.connect = "localhost:2181"
	}
	producer {
		metadata.broker.list = "localhost:9092"
	}
}
```

**Please Note**: the configuration values support various types of configuration, it's easier to tell from the configuration which key is supposed to be a duration or a buffer size for example. For this reason, some configuration keys are different from the core Kafka ones, for example `queue.buffering.max.messages` is `queue.buffering.max-messages` instead, or the `.ms` suffix is not used to configure durations.

#### Consumer Configuration

This is the list of configurations supported by the consumer:

```
group.id = "default"
zookeeper.connect = "localhost:2181"
socket.timeout = 30 seconds
socket.receive.buffer = 64B
fetch.message.max = 1M
num.consumer.fetchers = 1
auto.commit.enable = true
auto.commit.interval = 60 seconds
queued.max.message.chunks = 2
rebalance.max.retries = 4
fetch.min.bytes = 1
fetch.wait.max = 100 millis
rebalance.backoff = 2 seconds
refresh.leader.backoff = 200 millis
auto.offset.reset = "largest"
consumer.timeout = infinite
exclude.internal.topics = true
partition.assignment.strategy = "range"
client.id = ${kafka.consumer.group.id}
zookeeper.session.timeout = 6 seconds
zookeeper.connection.timeout = 6 seconds
zookeeper.sync.time = 2 seconds
offsets.storage = "zookeeper"
offsets.channel.backoff = 1 second
offsets.channel.socket.timeout = 10 seconds
offsets.commit.max.retries = 5
dual.commit.enabled = true
partition.assignment.strategy = "range"
```

#### Producer Configuration

This is the list of configurations supported by the producer:

```
metadata.broker.list = "localhost:9092"
request.timeout = 10 seconds
request.required.acks = 0
producer.type = "sync"
serializer.class = "kafka.serializer.DefaultEncoder"
key.serializer.class = ${kafka.producer.serializer.class}
partitioner.class = "kafka.producer.DefaultPartitioner"
compression.codec = "none"
compressed.topics = "null"
message.send.max.retries = 3
retry.backoff = 100 millis
topic.metadata.refresh.interval = 600 seconds
queue.buffering.max = 5 seconds
queue.buffering.max-messages = 10000
queue.enqueue.timeout = Inf
batch.num.messages = 200
send.buffer = 100B
client.id = ""
```

#### Custom Consumers / Producers Configuration

A Typesafe `Config` object can be passed into the `KafkaConsumer` and `KafkaProducer` constructors:

You can define separate blocks into your `application.conf`:

```
my-consumer-1 {
  group.id = "group-1"
}
my-consumer-2 {
  group.id = "group-2"
}

async-producer {
  producer.type = "async"
}
```

Then these configurations can be loaded like this:

```scala
import com.typesafe.config.ConfigFactory
val config = ConfigFactory.load

val consumerConfig1 = config.getConfig("my-consumer-1")
val consumerConfig2 = config.getConfig("my-consumer-2")

val asyncProducerConfig = config.getConfig("async-producer")
```

And they can be specified in the constructor:

```scala
val consumer1 = KafkaConsumer(TestTopic, consumerConfig1)
val consumer2 = KafkaConsumer(List(TestTopic, AnotherTestTopic), consumerConfig2)

val asyncProducer = KafkaProducer(asyncProducerConfig)
val asyncTestProducer = KafkaProducer(TestTopic, asyncProducerConfig)
```

### Custom Serialization

Custom serializers are supported and it's really easy to create one, the following are the serialization interfaces:

```scala
trait Serializer[-T] {
  def toBytes(obj: T): Array[Byte]
}

trait Deserializer[+T] {
  def fromBytes(bytes: Array[Byte]): T
}
```

As an example, take a look at the implementation of the Json4s serialization in `rx-kafka-json4s`:

```scala
import org.json4s.native.Serialization

class Json4sSerializer[T <: AnyRef](implicit formats: Formats) extends Serializer[T] {
  def toBytes(obj: T): Array[Byte] = Serialization.write(obj).getBytes
}

class Json4sDeserializer[T: Manifest](implicit formats: Formats) extends Deserializer[T] {
  def fromBytes(bytes: Array[Byte]): T = Serialization.read[T](new String(bytes)) 
}
```

License
-------

This software is licensed under the Apache 2 license, quoted below.

Copyright © 2015 Bruno Filippone <http://b-fil.com>

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    [http://www.apache.org/licenses/LICENSE-2.0]

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
