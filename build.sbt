lazy val root = Project("root", file("."))
  .settings(settings, publishArtifact := false, concurrentRestrictions in Global += Tags.limit(Tags.Test, 1))
  .aggregate(rxKafka, rxKafkaJson4s)

lazy val rxKafka = Project("rx-kafka-core", file("rx-kafka-core"))
  .settings(settings, libraryDependencies ++= Seq(
    "org.apache.kafka" %% "kafka" % "0.10.2.1" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12"),
    "org.apache.commons" % "commons-io" % "1.3.2",
    "com.typesafe" % "config" % "1.2.1",
    "io.reactivex" %% "rxscala" % "0.26.5",
    "io.bfil" %% "specs2-kafka" % "0.8.0" % "test",
    "org.specs2" %% "specs2-core" % "3.8.6" % "test",
    "org.specs2" %% "specs2-mock" % "3.8.6" % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test"
  ))

lazy val rxKafkaJson4s = Project("rx-kafka-json4s", file("rx-kafka-json4s"))
  .settings(settings, libraryDependencies ++= Seq(
    "org.json4s" %% "json4s-native" % "3.5.0",
    "io.bfil" %% "specs2-kafka" % "0.8.0" % "test",
    "org.specs2" %% "specs2-core" % "3.8.6" % "test",
    "org.specs2" %% "specs2-mock" % "3.8.6" % "test",
    "org.mockito" % "mockito-all" % "1.10.19" % "test",
    "org.hamcrest" % "hamcrest-all" % "1.3" % "test"
  ))
  .dependsOn(rxKafka)

lazy val settings = Seq(
  scalaVersion := "2.12.4",
  crossScalaVersions := Seq("2.12.4", "2.11.12"),
  organization := "io.bfil",
  organizationName := "Bruno Filippone",
  organizationHomepage := Some(url("http://bfil.io")),
  homepage := Some(url("https://github.com/bfil/rx-kafka")),
  licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
  developers := List(
    Developer("bfil", "Bruno Filippone", "bruno@bfil.io", url("http://bfil.io"))
  ),
  startYear := Some(2014),
  publishTo := Some("Bintray" at s"https://api.bintray.com/maven/bfil/maven/${name.value}"),
  credentials += Credentials(Path.userHome / ".ivy2" / ".bintray-credentials"),
  scmInfo := Some(ScmInfo(
    url(s"https://github.com/bfil/rx-kafka"),
    s"git@github.com:bfil/rx-kafka.git"
  ))
)
