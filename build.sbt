name := "Json_downloader"
version := "0.1"
scalaVersion := "2.13.7"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.6.17"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-http-core" % "10.2.6"
libraryDependencies += "com.lihaoyi" %% "upickle" % "1.4.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.17")

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.7"
libraryDependencies += "com.typesafe" % "config" % "1.4.1"