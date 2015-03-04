package at.linuxhacker.procmetrics.bin

import at.linuxhacker.procmetrics.lib._
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.pidstats._
import at.linuxhacker.procmetrics.converts.ProcConverters
import at.linuxhacker.procmetrics.monitor._

object ProcMetricsMain {

  def main( args: Array[String] ): Unit = {
    val dirList = ProcInfo.getDirList()
    val pids = ProcInfo.getCommandList( dirList )
    val filteredPids = ProcInfo.filterPids( ProcFilter.filter1 )( pids )
    val stats = ProcInfo.getStat( List( Schedstat, Netstat, Io, Statm, Status ), filteredPids )
    val globals = ProcInfo.getGlobals( List( GlobalUptime, Cpuinfo, Loadavg ) )

    println( ProcConverters.toJson( globals, stats ) )
  }
}

object ProcMetricsMonitor {
  def main( args: Array[String] ): Unit = {
    val dirList = ProcInfo.getDirList()
    val pids = ProcInfo.getCommandList( dirList )
    val filteredPids = ProcInfo.filterPids( ProcFilter.filter1 )( pids )
    val stats = ProcInfo.getStat( List( Schedstat, Netstat, Io, Statm, Status ), filteredPids )
    
    val table = MonitorFunctions.transformToColumns(stats, List( 
        Column( "netstat", "in_octets" ),
        Column( "status", "VmSize" )
    ))
    
    println( table )
  }
}
