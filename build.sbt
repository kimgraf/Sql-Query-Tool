name := """jdbc_source"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers ++= Seq("Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
                  "JTO snapshots" at "https://raw.github.com/jto/mvn-repo/master/snapshots"
)

libraryDependencies ++= Seq(
  "io.github.jto" %% "validation-core" % "1.0-1c770f4",
  "com.google.inject" % "guice" % "3.0",
  "javax.inject" % "javax.inject" % "1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0-SNAPSHOT",
//  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  jdbc,
  anorm,
  cache,
  ws
)
