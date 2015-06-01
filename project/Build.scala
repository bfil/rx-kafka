import sbt._
import Keys._
import com.bfil.sbt._

object ProjectBuild extends BFilBuild {

  val buildVersion = "0.2.0-SNAPSHOT"

  lazy val root = BFilRootProject("root", file("."))
    .aggregate(rxKafka, rxKafkaJson4s)
    .settings(concurrentRestrictions in Global += Tags.limit(Tags.Test, 1))

  lazy val rxKafka = BFilProject("rx-kafka-core", file("rx-kafka-core"))
    .settings(libraryDependencies ++= Dependencies.core(scalaVersion.value))

  lazy val rxKafkaJson4s = BFilProject("rx-kafka-json4s", file("rx-kafka-json4s"))
    .settings(libraryDependencies ++= Dependencies.json4s(scalaVersion.value))
    .dependsOn(rxKafka)
}

object Dependencies {
  
  def core(scalaVersion: String) = Seq(
    "org.apache.kafka" %% "kafka" % "0.8.2.1",
    "org.apache.commons" % "commons-io" % "1.3.2",
    "com.typesafe" % "config" % "1.2.1",
    "io.reactivex" %% "rxscala" % "0.23.1",
    "com.bfil" %% "specs2-kafka" % "0.2.0-SNAPSHOT" % "test",
    "org.specs2" %% "specs2-core" % "2.4.17" % "test",
    "org.specs2" %% "specs2-mock" % "2.4.17" % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test")

  def json4s(scalaVersion: String) = Seq(
    "org.json4s" %% "json4s-native" % "3.2.11",
    "com.bfil" %% "specs2-kafka" % "0.2.0-SNAPSHOT" % "test",
    "org.specs2" %% "specs2-core" % "2.4.17" % "test",
    "org.specs2" %% "specs2-mock" % "2.4.17" % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test")
}