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
          ValueFactory.create( content( 0 ).split( " " )( 0 ).toFloat ) ) ) ) )
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
        		ValueFactory.create( processorCount ) ),
        	ProcValue( "model_name",
        	    ValueFactory.create( modelName ) ),
        	ProcValue( "cpu_mhz", 
        	    ValueFactory.create( cpuMhz ) )
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
        		ValueFactory.create( parts( 0 ).toFloat ) ),
        	ProcValue( "load_5m",
        	    ValueFactory.create( parts( 1 ).toFloat ) ),
        	ProcValue( "load_10m", 
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
  def getFilename( ): String = { "net/dev" }
  def getStat( content: List[String] ): List[ProcGlobal] = {
    case class Netstat( name: String, recbytes: Float, transbytes: Float )
    if ( content.length > 0 ) {
      val pattern ="""^([a-zA-Z0-9]+):""".r
      val extract = content.map( rec => {
        val parts = rec.replaceAll( """^( +)""", "").replaceAll( """( +)""", " " ).split( " " )
        parts( 0 ) match {
          case pattern( name ) => Some( Netstat( name, parts( 1 ).toFloat, parts( 9 ).toFloat ) )
          case _ => None
        }
      } )
      extract
      	.filter( x => x match { case Some(y) => true case _ => false } )
      	.map( _ match { case Some(y) => y case _ => throw new Exception( "Not possible" ) } )
      	.map( x => ProcGlobal( x.name, List( 
      	    ProcValue( "recv_bytes", ValueFactory.create( x.recbytes ) ),
      	    ProcValue( "trans_bytes", ValueFactory.create( x.transbytes ) ) ) ) )
    } else {
      List[ProcGlobal]( )
    }
  }
}