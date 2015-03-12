package at.linuxhacker.procmetrics.lib

import at.linuxhacker.procmetrics.values._
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.pidstats._
import java.io.File
import at.linuxhacker.procmetrics.sysfs.NetMac

object ProcFilter {
  def patternFilter( pattern: String, pids: List[Pid] ): List[Pid] = {
    pids.filter( pid => if ( pid.cmdline contains pattern ) true else false )
  }

  def nullFilter( pattern: String, pids: List[Pid] ): List[Pid] = {
    pids
  }
  
}

class ProcInfo( val root: String ) {
  private val pidPattern = ( root + """proc/([0-9]+)""" ).r

  def getPidDirList( ): List[String] = {
    val path = new File( root + "proc" )
    path.listFiles.filter( _.isDirectory ).filter( rec => {
      rec.toString match {
        case pidPattern( pid ) => true
        case _ => false
      }
    } )
      .map( x => x.toString match { case pidPattern( pid ) => pid } )
      .map( _.toString ).toList
  }

  def getCommandList( pids: List[String] ): List[Pid] = {
    pids.map { pid =>
      {
        val records = getFileContent( "proc/" + pid + "/cmdline" )
        if ( records.length > 0 ) {
          Some( Pid( pid, records( 0 ).replace( "\u0000", " " ) ) )
        } else {
          None
        }

      }
    }.filter( x => x match { case Some( _ ) => true; case _ => false } )
      .map( x => x match { case Some( s ) => s case _ => null } )
  }

  def filterPids( f: ( String, List[Pid] ) => List[Pid] )( pattern: String, pids: List[Pid] ): List[Pid] = {
    f( pattern, pids )
  }

  def getStat( stats: List[Stat], pids: List[Pid] ): List[ProcCategory] = {
    stats.map {
      stat =>
        {
          pids.map {
            pid =>
              {
                val records = getFileContent(  "proc/" + pid.pid + "/" + stat.getFilename )
                stat.getStat( pid, records )
              }
          }
        }
          .filter( x => x match { case Some( s ) => true case _ => false } )
          .map( x => x match { case Some( s ) => s case _ => null } )
    }.flatten
  }

  def getGlobals( globals: List[Global] ): List[ProcGlobal] = {
    globals.map { g =>
      val records = getFileContent( g.getFilename )
      g.getStat( records )
    }.filter( x => x match { case Some( s ) => true case _ => false } )
      .map( x => x match { case Some( s ) => s case _ => null } )
  }
  
  def getMultiGlobals( globals: List[MultiGlobalStatsSpecifier] ): List[MultiGlobalStatsResult] = {
    globals.map( spec => {
      val g = spec.stats
      val records = getFileContent( g.getFilename )
      val res = g.getStat( records )
      MultiGlobalStatsResult( spec.name, res )
    })
  }
  
  def getSysfsNetMac( netDeviceNames: List[String] ): List[ProcGlobal] = {
    netDeviceNames.map( netDeviceName => {
      val content = getFileContent( NetMac.getFilename( netDeviceName ) )
      if ( content.length > 0 )
        NetMac.getStat( netDeviceName, content )
      else
        None
    } ).filter( x => x != None )
       .map( i => i match { case Some( s ) => s case _ => throw new Exception( "Unbelievable" ) } )
  }
  
  def getFileContent( filename: String ): List[String] = {
    try {
      val source = scala.io.Source.fromFile( root + filename )
      var records = source.getLines.toList
      source.close
      records

    } catch {
      // TODO: not ideal! What we can do?
      case e: Exception => println( e.getMessage( ) ); List( )
      case _: Throwable => List()
    }
  }

}



