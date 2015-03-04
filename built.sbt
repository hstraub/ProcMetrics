name := "ProcMetrics"

version := "0.1"

scalaVersion := "2.11.2"


mergeStrategy in assembly := { 
	case PathList( "META-INF", "MANIFEST.MF" ) => MergeStrategy.discard
	case _ => MergeStrategy.first
}

mainClass in assembly := Some( "at.linuxhacker.procmetrics" )

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies += "org.specs2" %% "specs2-core" % "2.4.3" % "test"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.0-M2"

