name := "FoodMacroAPI"

version := "1.0"

scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "org.playframework.anorm" %% "anorm" % "2.7.0",
  "com.typesafe.play" %% "play-slick" % "5.1.0",
  "org.postgresql" % "postgresql" % "42.5.1",
  "com.typesafe.akka" %% "akka-http" % "10.2.10",
  "com.softwaremill.sttp.client3" %% "core" % "3.8.15",
  "com.softwaremill.sttp.client3" %% "akka-http-backend" % "3.8.15", // Sttp with Akka HTTP
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.playframework.twirl" %% "twirl-api" % "2.0.7"
)

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.EarlySemVer
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"


enablePlugins(PlayScala)
libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.8.15"
libraryDependencies += "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % "3.8.15"

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-java8-compat" % VersionScheme.EarlySemVer
libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.0"

