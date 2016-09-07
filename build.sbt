name := """server"""
version := "1.1"
organization := "com.gitpitch"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  javaWs,
  filters
)

libraryDependencies ++= Seq(
  "com.typesafe.netty" % "netty-reactive-streams-http" % "1.0.6",
  "com.google.guava" % "guava" % "19.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "4.0",
  "org.yaml" % "snakeyaml" % "1.17",
  "commons-io" % "commons-io" % "2.5"
)
