package at.linuxhacker.procmetrics.bin

import at.linuxhacker.procmetrics.lib._
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.pidstats._

object ProcMetricsMain {

  def main( args: Array[String] ): Unit = {
    val dirList = ProcInfo.getDirList()
    val pids = ProcInfo.getCommandList( dirList )
    val filteredPids = ProcInfo.filterPids( ProcFilter.filter1 )( pids )
    val stats = ProcInfo.getStat( List( Schedstat, Netstat, Io, Statm ), filteredPids )
    val globals = ProcInfo.getGlobals( List( GlobalUptime, Cpuinfo, Loadavg ) )
    stats.foreach( println )
    globals.foreach( println )
  }
}
