package at.linuxhacker.procmetrics.global

import at.linuxhacker.procmetrics.values._

abstract class Global {
  def getFilename(): String
  def getStat( content: List[String] ): Option[ProcGlobal]
}

object GlobalUptime extends Global {
  def getFilename(): String = { "proc/uptime" }
  def getStat( content: List[String] ): Option[ProcGlobal] = {
    if ( content.length > 0 ) {
      Some( ProcGlobal( "uptime",
        List( ProcValueFactory.create( "uptime_sec",
          ValueFactory.create( content( 0 ).split( " " )( 0 ).toFloat ) ) ) ) )
    } else {
      None
    }
  }

}

object Cpuinfo extends Global {
  def getFilename( ): String = { "proc/cpuinfo" }
  def getStat( content: List[String] ): Option[ProcGlobal] = {
    if ( content.length > 0 ) {
      var processorCount = content.filter( _.startsWith( "processor") ).length;
      var modelName = content.filter( _.startsWith("model name") )(0).split( ":" )(1).replaceAll( """^( *)""", "" )
      var cpuMhz = content.filter( _.startsWith( "cpu MHz" ) )(0).split( ":" )(1).replaceAll( """^( *)""", "" ).toFloat
      Some( ProcGlobal( "cpuinfo",
        List( 
            ProcValueFactory.create( "processor_count",
        		ValueFactory.create( processorCount ) ),
        	ProcValueFactory.create( "model_name",
        	    ValueFactory.create( modelName ) ),
        	ProcValueFactory.create( "cpu_mhz", 
        	    ValueFactory.create( cpuMhz ) )
        	)
      ) )
    } else {
      None
    }
  }
}

object Loadavg extends Global {
  def getFilename( ): String = { "proc/loadavg" }
  def getStat( content: List[String] ): Option[ProcGlobal] = {
    if ( content.length > 0 ) {
      val parts = content(0).split( " " )
      Some( ProcGlobal( "loadavg",
        List( 
            ProcValueFactory.create( "load_1m",
        		ValueFactory.create( parts( 0 ).toFloat ) ),
        	ProcValueFactory.create( "load_5m",
        	    ValueFactory.create( parts( 1 ).toFloat ) ),
        	ProcValueFactory.create( "load_10m", 
        	    ValueFactory.create( parts( 2 ).toFloat ) )
        	)
      ) )
    } else {
      None
    }
  }
}


abstract class MultiGlobalStats {
  def getFilename(): String
  def getStat( content: List[String] ): List[ProcGlobal]
}

case class MultiGlobalStatsSpecifier( name: String, stats: MultiGlobalStats )
case class MultiGlobalStatsResult( name: String, result: List[ProcGlobal] )

object NetDev extends MultiGlobalStats {
  def getFilename( ): String = { "proc/net/dev" }
  def getStat( content: List[String] ): List[ProcGlobal] = {
    case class Netstat( name: String, recbytes: Float, transbytes: Float )
    if ( content.length > 0 ) {
      val pattern ="""^([a-zA-Z0-9]+):(.*)""".r
      val extract = content.map( rec => {
        val leftRight = rec.replaceAll( """^( +)""", "").replaceAll( """( +)""", " " ).split( ":" )
        if ( leftRight.length == 2 ) {
          val parts = leftRight(1).replaceAll( """^( +)""", "").split( " " )
          Some( Netstat( leftRight(0), parts( 0 ).toFloat, parts( 8 ).toFloat ) )
        } else {
          None
        }
      } )
      extract
      	.filter( x => x match { case Some(y) => true case _ => false } )
      	.map( _ match { case Some(y) => y case _ => throw new Exception( "Not possible" ) } )
      	.map( x => ProcGlobal( x.name, List( 
      	    ProcValueFactory.create( "recv_bytes", ValueFactory.create( x.recbytes ) ),
      	    ProcValueFactory.create( "trans_bytes", ValueFactory.create( x.transbytes ) ) ) ) )
    } else {
      List[ProcGlobal]( )
    }
  }
}