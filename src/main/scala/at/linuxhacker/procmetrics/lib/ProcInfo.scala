package at.linuxhacker.procmetrics.lib

import at.linuxhacker.procmetrics.values._
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.pidstats._
import java.io.File

object ProcFilter {
  def filter1( pids: List[Pid] ): List[Pid] = {
    pids.filter( pid => if ( pid.cmdline contains "gvfs" ) true else false )
  }
}

object ProcInfo {
  private val pidPattern = """/proc/([0-9]+)""".r

  def getDirList(): List[String] = {
    val path = new File( "/proc" )
    path.listFiles.filter( _.isDirectory ).filter( rec => {
      rec.toString match {
        case pidPattern( pid ) => true
        case _ => false
      }
    } ).map( _.toString ).toList
  }

  def getCommandList( dirList: List[String] ): List[Pid] = {
    dirList.map { dir =>
      {
        val records = getFileContent( dir + "/cmdline" )
        if ( records.length > 0 ) {
          dir match {
            case pidPattern( pid ) => Some( Pid( pid, records( 0 ) ) )
            case _ => None
          }
        } else {
          None
        }

      }
    }.filter( x => x match { case Some( _ ) => true; case _ => false } )
      .map( x => x match { case Some( s ) => s case _ => null } )
  }

  def filterPids( f: List[Pid] => List[Pid] )( pids: List[Pid] ): List[Pid] = {
    f( pids )
  }

  def getStat( stats: List[Stat], pids: List[Pid] ): List[List[ProcCategory]] = {
    stats.map {
      stat =>
        {
          pids.map {
            pid =>
              {
                val records = getFileContent( "/proc/" + pid.pid + "/" + stat.getFilename )
                stat.getStat( pid, records )
              }
          }
        }.filter( x => x match { case Some( s ) => true case _ => false } )
          .map( x => x match { case Some( s ) => s case _ => null } )
    }
  }

  def getGlobals( globals: List[Global] ): List[ProcGlobal] = {
    globals.map { g =>
      val records = getFileContent( "/proc/" + g.getFilename )
      g.getStat( records )
    }.filter( x => x match { case Some( s ) => true case _ => false } )
      .map( x => x match { case Some( s ) => s case _ => null } )
  }

  private def getFileContent( filename: String ): List[String] = {
    try {
      val source = scala.io.Source.fromFile( filename )
      var records = source.getLines.toList
      source.close
      records

    } catch {
      case _: Throwable => List()
    }
  }

}



