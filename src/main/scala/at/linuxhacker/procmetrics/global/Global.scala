package at.linuxhacker.procmetrics.global

import at.linuxhacker.procmetrics.values._

abstract class Global {
  def getFilename(): String
  def getStat( content: List[String] ): Option[ProcGlobal]
}
object GlobalUptime extends Global {
  def getFilename(): String = { "uptime" }
  def getStat( content: List[String] ): Option[ProcGlobal] = {
    if ( content.length > 0 ) {
      Some( ProcGlobal( "uptime",
        List( ProcValue( "uptime_sec",
          ProcValueFloat( content( 0 ).split( " " )( 0 ).toFloat ) ) ) ) )
    } else {
      None
    }
  }

}

object Cpuinfo extends Global {
  def getFilename( ): String = { "cpuinfo" }
  def getStat( content: List[String] ): Option[ProcGlobal] = {
    if ( content.length > 0 ) {
      var processorCount = content.filter( _.startsWith( "processor") ).length;
      var modelName = content.filter( _.startsWith("model name") )(0).split( ":" )(1).replaceAll( """^( *)""", "" )
      var cpuMhz = content.filter( _.startsWith( "cpu MHz" ) )(0).split( ":" )(1).replaceAll( """^( *)""", "" ).toFloat
      Some( ProcGlobal( "cpuinfo",
        List( 
            ProcValue( "processor_count",
        		ProcValueInt( processorCount ) ),
        	ProcValue( "model_name",
        	    ProcValueString( modelName ) ),
        	ProcValue( "cpu_mhz", 
        	    ProcValueFloat( cpuMhz ) )
        	)
      ) )
    } else {
      None
    }
  }
}

object Loadavg extends Global {
  def getFilename( ): String = { "loadavg" }
  def getStat( content: List[String] ): Option[ProcGlobal] = {
    if ( content.length > 0 ) {
      val parts = content(0).split( " " )
      Some( ProcGlobal( "loadavg",
        List( 
            ProcValue( "load_1m",
        		ProcValueFloat( parts( 0 ).toFloat ) ),
        	ProcValue( "load_5m",
        	    ProcValueFloat( parts( 1 ).toFloat ) ),
        	ProcValue( "load_10m", 
        	    ProcValueFloat( parts( 2 ).toFloat ) )
        	)
      ) )
    } else {
      None
    }
  }
}