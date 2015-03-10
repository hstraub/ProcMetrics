package at.linuxhacker.procmetrics.bin

import at.linuxhacker.procmetrics.lib._
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.pidstats._
import at.linuxhacker.procmetrics.converts.ProcConverters
import at.linuxhacker.procmetrics.monitor._
import at.linuxhacker.procmetrics.values._

object ProcMetricsMain {

  def main( args: Array[String] ): Unit = {
    val procInfo = new ProcInfo( "/proc/" )
    val dirList = procInfo.getDirList()
    val pids = procInfo.getCommandList( dirList )
    val filteredPids = procInfo.filterPids( ProcFilter.patternFilter )( "g", pids )
    val stats = procInfo.getStat( List( Schedstat, Io, Statm, Status, PStat ), filteredPids )
    val globals = procInfo.getGlobals( List( GlobalUptime, Cpuinfo, Loadavg ) )
    val multiGlobalsStat = procInfo.getMultiGlobals( List( MultiGlobalStatsSpecifier( "NetDev", NetDev ) ) )

    println( ProcConverters.toJson( globals, stats, multiGlobalsStat ) )
  }
}

object ProcMetricsMonitor {
  def main( args: Array[String] ): Unit = {
    val columns =  List( 
        Column( "netstat", "in_octets" ),
        Column( "netstat", "out_octets" ),
        Column( "status", "VmSize" ),
        Column( "io", "read_bytes" ),
        Column( "io", "write_bytes" ),
        Column( "stat", "cpu_sum_sec" ) )


    while ( true ) {
      val procInfo = new ProcInfo( "/proc/" )
      val dirList = procInfo.getDirList()
      val pids = procInfo.getCommandList( dirList )
      val filteredPids = {
        if ( args.length > 0 )
          procInfo.filterPids( ProcFilter.patternFilter )( args(0), pids )
        else
          procInfo.filterPids( ProcFilter.nullFilter )( "", pids )
      }
      val filteredPidsToCmdlineMap = filteredPids.map( x => x.pid -> x.cmdline  ).toMap
      val t1 = MonitorFunctions.transformToColumns(
        procInfo.getStat( List( Schedstat, Io, Statm, Status, PStat ),
          filteredPids ),
        columns )

      Thread.sleep( 1000 )

      val t2 = MonitorFunctions.transformToColumns(
        procInfo.getStat( List( Schedstat, Io, Statm, Status, PStat ),
          filteredPids ),
        columns )

      val r = MonitorFunctions.diffTables( t1, t2 )
      val x = r.filter( _._2.filter( _.value.asInstanceOf[ProcFloatValue].value != 0f ).length > 0 )
      x.foreach( pid => {
        val withoutNull = pid._2.filter( _.value.asInstanceOf[ProcFloatValue].value != 0f )
        if ( withoutNull.length > 0 ) {
          val t = withoutNull.map( x => x.name + ": " + x.value.asInstanceOf[ProcFloatValue].value )
          val cmdline = filteredPidsToCmdlineMap( pid._1 )
          println( "PID: %8s c: %-20s v: %s"
            .format( pid._1,
              { if ( cmdline.length() <= 20 ) cmdline else cmdline.substring( 0, 19 ) },
              t.foldLeft( "" )( ( t, c ) => t + " " + c ) ) )
        }
      } )
      println ( )
    }
  }
    
}
