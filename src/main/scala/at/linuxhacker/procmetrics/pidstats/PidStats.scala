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
      Some( ProcCategory( pid, "schedstat",
        List(
          ProcValue( "used_sec", ProcValueX[Float]( used_sec ) ),
          ProcValue( "wait_runqueue_sec", ProcValueX[Float]( wait_runqueue_sec ) ) ) ) )
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
          ProcValue( "in_octets", ProcValueX[Float]( inOctets ) ),
          ProcValue( "out_octets", ProcValueX[Float]( outOcteds ) ) ) ) )
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
        ProcValue( parts( 0 ), ProcValueX[Float]( parts( 1 ).toFloat ) )
      }
    }
    if ( values.length > 0 ) {
      Some( ProcCategory( pid, "io", values ) )
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
      Some( ProcCategory( pid, "statm", List( 
          ProcValue( "size", ProcValueX[Int]( size ) ),
          ProcValue( "resident", ProcValueX[Int]( resident ) ),
          ProcValue( "share", ProcValueX[Int]( share ) ),
          ProcValue( "data", ProcValueX[Int]( data ) )          
      ) ) )
    } else {
      None
    }
  }
}

object Status extends Stat {
  def getFilename() = { "status" }

  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory] = {
    if ( content.length > 0 ) {
      
      val vms = content.filter( _.startsWith( "Vm" ) ).map( rec => {
        val parts = rec.split( ":" )
        val vmName = parts(0)
        val memparts = parts(1).replaceAll( """^(\s+)""", "" ).split( " " )
        val vmValue = memparts(0).toInt
        val factor = memparts(1) match {
          case "kB" => 1024
          case "MB" => 1024 * 1024
          case _ => 1
        }
        ProcValue( vmName, ProcValueX[Int]( vmValue * factor ) )
      })

      val threadCount = ProcValue( "thread_count", 
          ProcValueX[Int]( content.filter( _.startsWith( "Threads:" ) )(0).split( ":" )(1).replaceAll( """^(\s+)""", "" ).toInt ) )
      
      val values = threadCount :: vms 
      Some( ProcCategory( pid, "status", values ) )          
    } else {
      None
    }
  }
  
}
