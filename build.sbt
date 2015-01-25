name := """jdbc_source"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers ++= Seq("Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
                  "JTO snapshots" at "https://raw.github.com/jto/mvn-repo/master/snapshots",
                  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  Resolver.url("Objectify Play Repository", url("http://deadbolt.ws/releases/"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "io.github.jto" %% "validation-core" % "1.0-1c770f4",
  "com.google.inject" % "guice" % "3.0",
  "javax.inject" % "javax.inject" % "1",
  "be.objectify" %% "deadbolt-scala" % "2.3.2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  "org.reactivemongo" %% "reactivemongo-extensions-json" % "0.10.5.0.0.akka23"
)

//resolvers += Resolver.url("Objectify Play Repository", url("http://deadbolt.ws/releases/"))(Resolver.ivyStylePatterns)
