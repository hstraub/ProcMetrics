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
          ProcValue( "used_sec", ValueFactory.create( used_sec ) ),
          ProcValue( "wait_runqueue_sec", ValueFactory.create( wait_runqueue_sec ) ) ) ) )
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
        ProcValue( parts( 0 ), ValueFactory.create( parts( 1 ).toFloat ) )
      }
    }
    if ( values.length > 0 ) {
      Some( ProcCategory( pid, "io", values ) )
    } else {
      None
    }
  }
}

object PStat extends Stat {
  def getFilename() = { "stat" }

  def getStat( pid: Pid, content: List[String] ): Option[ProcCategory] = {
    if ( content.length > 0 ) {
      val parts = content(0).split( " " )
      val user =  parts(13).toInt
      val sys = parts(14).toInt
      val sum = user + sys
      Some( ProcCategory( pid, "stat", List(
          ProcValue( "cpu_user_sec",  ValueFactory.create( user ) ),
          ProcValue( "cpu_sys_sec", ValueFactory.create( sys ) ),
          ProcValue( "cpu_sum_sec", ValueFactory.create( sum ) )
      ) ) )
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
          ProcValue( "size", ValueFactory.create( size ) ),
          ProcValue( "resident", ValueFactory.create( resident ) ),
          ProcValue( "share", ValueFactory.create( share ) ),
          ProcValue( "data", ValueFactory.create( data ) )          
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
        val vmValue = memparts(0).toFloat
        val factor = memparts(1) match {
          case "kB" => 1024f
          case "MB" => 1024f * 1024f
          case _ => 1
        }
        ProcValue( vmName, ValueFactory.create( vmValue * factor ) )
      })

      val threadCount = ProcValue( "thread_count", 
          ValueFactory.create( content.filter( _.startsWith( "Threads:" ) )(0).split( ":" )(1).replaceAll( """^(\s+)""", "" ).toInt ) )
      
      val values = threadCount :: vms 
      Some( ProcCategory( pid, "status", values ) )          
    } else {
      None
    }
  }
  
}
