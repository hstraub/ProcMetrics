name := "ProcMetrics"

version := "0.1"

scalaVersion := "2.11.6"

mergeStrategy in assembly := { 
	case PathList( "META-INF", "MANIFEST.MF" ) => MergeStrategy.discard
	case _ => MergeStrategy.first
}

mainClass in assembly := Some( "at.linuxhacker.procmetrics" )

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases" )

libraryDependencies ++= Seq( 
  "com.typesafe.play" %% "play-json" % "2.4.0-M2",
  "com.github.scopt" %% "scopt" % "3.3.0",
  "org.scalaj" %% "scalaj-http" % "1.1.4",
  "org.specs2" %% "specs2-core" % "2.4.3" % "test",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "junit" % "junit" % "4.11" % "test"
)

