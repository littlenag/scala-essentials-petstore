// Read the docs at http://www.scala-sbt.org/0.13/docs/

organization := "scala-essentials" // equivalent to maven groupId
name         := "petstore"         // equivalent to maven artifactId
version      := "0.0.1-SNAPSHOT"   // follows maven versioning rules

scalaVersion := "2.12.1"           // Will output JDK8 bytecode

scalacOptions ++= Seq(
  "-encoding", "UTF-8",            // Specify character encoding used by source files.
  "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
  "-deprecation",                  // Emit warning and location for usages of deprecated APIs.
  "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
  "-explaintypes",                 // Explain type errors in more detail.
  "-language:higherKinds",         // enable higher-kinded types
  "-language:postfixOps",          // enable post-fix operations
  "-Xfuture",                      // hide procedure syntax
  "-Xlint:missing-interpolator",
  "-Yno-adapted-args",             // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ywarn-unused-import",
  "-Ywarn-unused",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen"
  //"-Xfatal-warnings"               // All warnings become errors and stop compilation
)

// Specifiy these once so our dependencies don't get out of sync
val akkaVersion = "2.4.17"
val akkaHttpVersion = "10.0.4"
val slickVersion = "3.2.0"

libraryDependencies ++= Seq(
  // Our HTTP server: http://doc.akka.io/docs/akka-http/current/index.html
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

  "com.typesafe.play" %% "play-json" % "2.6.0-M5",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.14.0",

  // Slick docs: http://slick.lightbend.com/doc/3.2.0/
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,

//  "org.liquibase" % "liquibase-core" % "3.5.3",
//  "com.mattbertolini" % "liquibase-slf4j" % "2.0.0" % "runtime",

  "com.h2database" % "h2" % "1.4.194",

  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",

//  "joda-time" % "joda-time" % "2.9.7",

  "ch.qos.logback" % "logback-classic" % "1.2.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

  // HOCON config: https://github.com/typesafehub/config/blob/master/HOCON.md
  "com.typesafe" % "config" % "1.3.1",

  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test"
)

// Prevent CTRL-C from killing SBT while the 'run' task is active.
cancelable in Global := true
fork in run := true
