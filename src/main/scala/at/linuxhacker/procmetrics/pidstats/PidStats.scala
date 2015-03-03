package at.linuxhacker.procmetrics.pidstats

import at.linuxhacker.procmetrics.values._

abstract class Stat {
  def getFilename(): String
  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory]
}

object Schedstat extends Stat {

  def getFilename() = {
    "schedstat"
  }

  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory] = {
    if ( content.length > 0 ) {
      val parts = content( 0 ).split( " " )
      val used_sec = parts( 0 ).toFloat / 1000
      val wait_runqueue_sec = parts( 1 ).toFloat / 1000
      Some( ProcCategory( pid, "CPU",
        List(
          ProcValue( "used_sec", ProcValueFloat( used_sec ) ),
          ProcValue( "wait_runqueue_sec", ProcValueFloat( wait_runqueue_sec ) ) ) ) )
    } else {
      None
    }
  }
}

object Netstat extends Stat {
  def getFilename() = { "net/netstat" }

  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory] = {
    if ( content.length > 0 ) {
      val ipExt = content.filter( rec => rec.startsWith( "IpExt:" ) )
      val parts = ipExt( 1 ).split( " " )
      val inOctets = parts( 7 ).toFloat
      val outOcteds = parts( 8 ).toFloat
      Some( ProcCategory( pid, "netstat",
        List(
          ProcValue( "in_octets", ProcValueFloat( inOctets ) ),
          ProcValue( "out_octets", ProcValueFloat( outOcteds ) ) ) ) )
    } else {
      None
    }
  }
}

object Io extends Stat {
  def getFilename() = { "io" }

  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory] = {
    val values = content.map { rec =>
      {
        val parts = rec.split( ":" )
        ProcValue( parts( 0 ), ProcValueFloat( parts( 1 ).toFloat ) )
      }
    }
    if ( values.length > 0 ) {
      Some( ProcCategory( pid, "diskio", values ) )
    } else {
      None
    }
  }
}

object Statm extends Stat {
  def getFilename() = { "statm" }

  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory] = {
    if ( content.length > 0 ) {
      val parts = content(0).split( " " )
      val size = parts( 0 ).toInt
      val resident = parts( 1 ).toInt
      val share = parts( 2 ).toInt
      val data = parts( 5 ).toInt
      Some( ProcCategory( pid, "diskio", List( 
          ProcValue( "size", ProcValueInt( size ) ),
          ProcValue( "resident", ProcValueInt( resident ) ),
          ProcValue( "share", ProcValueInt( share ) ),
          ProcValue( "data", ProcValueInt( data ) )          
      ) ) )
    } else {
      None
    }
  }
}
