package at.linuxhacker.procmetrics.bin

import at.linuxhacker.procmetrics.lib._
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.pidstats._
import at.linuxhacker.procmetrics.converts.ProcConverters
import at.linuxhacker.procmetrics.monitor._
import at.linuxhacker.procmetrics.values._
import scopt._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import at.linuxhacker.procmetrics.couchdb.CouchDb
import at.linuxhacker.procmetrics.lib._

object Statistics {

  case class Result( filteredPids: List[Pid], globals: List[ProcGlobal], stats: List[ProcCategory],
    multiGlobalsStat: List[MultiGlobalStatsResult], sysStat: List[ProcGlobal] ) {

    def toJson( ): JsObject = {
      ProcConverters.toJson( globals, stats, multiGlobalsStat, sysStat )
    }

  }
  
  def get( filter: ( String, List[Pid] )  => List[Pid], filterPattern: String ): Result = {
    val procInfo = new ProcInfo( "/" )
    val dirList = procInfo.getPidDirList()
    val pids = procInfo.getCommandList( dirList )
    val filteredPids = procInfo.filterPids( filter )( filterPattern, pids )
    val stats = procInfo.getStat( List( Schedstat, Io, Statm, Status, PStat ), filteredPids )
    val globals = procInfo.getGlobals( List( GlobalUptime, Cpuinfo, Loadavg ) )
    val multiGlobalsStat = procInfo.getMultiGlobals( List( MultiGlobalStatsSpecifier( "NetDev", NetDev ) ) )
    val netDeviceNames = multiGlobalsStat
    	.filter( x => x.name == "NetDev" )
    	.map( x => x.result.map( y => y.category ) )
    	.flatten
    val sysStat = procInfo.getSysfsNetMac( netDeviceNames )
    Result( filteredPids, globals, stats, multiGlobalsStat, sysStat )
  }
  
}

object ProcMetricsMain {

  def main( args: Array[String] ): Unit = {
    val filterPattern = {
      if ( args.length > 0 )
        args(0)
      else
        ""
    }
    
    println( Statistics.get( ProcFilter.patternFilter, filterPattern ).toJson.toString )
  }
}

object ProcMetricsQemu {
  
  def main( args: Array[String] ): Unit = {
    val result =Statistics.get( ProcFilter.patternFilter, "qemu" )
    val data1 = result.toJson
    
    val qemuDescriptions = result.filteredPids.map( x => ProcGlobal( x.pid, extractValues( x.cmdline ) ) )
    val data2 = ProcConverters.globalsToJson( qemuDescriptions )
    val jsonTransformer = (__).json.update( 
        __.read[JsObject].map { o =>  
          o ++ Json.obj( "qemu_parameter" -> JsObject( data2 ) ) } )
          
    data1.transform( jsonTransformer ) match {
      case JsSuccess( result, x ) => println ( result )
      case _ => throw new Exception ( "unbelievable..." )
    }
  }
  
  private case class ExtractInfo( name: String, 
      regex: scala.util.matching.Regex, f:( String ) => ProcGenValue )
  
  private val infoList = List( 
      ExtractInfo( "memory", """.*-m (\d+).*""".r, 
          ( x: String ) => ValueFactory.create( x.toInt ) ),
      ExtractInfo( "name", """.*-name ([a-zA-Z1-9\-_]+).*""".r, 
          ( x: String ) => ValueFactory.create( x ) ),
      ExtractInfo( "mac", """mac=([^, ]+)""".r,
          ( x: String ) => ValueFactory.create( x ) ) )

  //val x = ( item.regex findAllMatchIn cmdline ).map( x => x.group(1) ).toList
  private def extractValues( cmdline: String ): List[ProcValue] = {
    infoList.map( item => {
      val result = ( item.regex findAllMatchIn cmdline ).map( x => x.group(1) ).toList
      val x = result.length match {
        case 1 =>
          ProcValueFactory.create( item.name, item.f( result(0) ) )
        case x if x > 1 =>
          ProcValueFactory.create( item.name, { result.map( i => item.f( i ) ) } )
        case _ =>
          throw new Exception( "Cannot find regex: " +item.regex + " in commandline: " + cmdline )
      }
      x
    } )  
  }
}

object ProcMetricsCouchDb {
  
  case class Config( 
      couchdbUrl: String = "http://localhost:5984",
      database: String = "mydatabase",
      docType: String = "metrics1",
      docNamePrefix: String = "m1",
      modules: Seq[String] = Seq( ),
      help: Boolean = false,
      filter: String = ""
      //regexFilter: Boolean = false
      )
  
  def main( args: Array[String] ): Unit = {
    val parser = new OptionParser[Config]( "procmetrics-couchdb" ) {
      head( "ProcMetricsCouchDb", "0.1" )
      opt[String]( 'c', "couchdb-url" ) action { ( x, c )
        => c.copy( couchdbUrl = x ) } text( "CouchDb Url: http://localhost:5984/" )
      opt[String]( 'd', "database" ) action{ ( x, c )
        => c.copy( database = x) } text( "Database name" )
      opt[String]( 't', "doc-type" ) action{ ( x, c )
        => c.copy( docType = x ) } text( "Document type name, for example: metrics1" )
      opt[String]( 'p', "doc-name-prefix" ) action{ ( x, c )
        => c.copy( docNamePrefix = x ) } text( "Docment name prefix, for example: m1" )
      opt[Seq[String]]( 'm', "modules" ) action { ( x, c )
        => c.copy( modules = x ) } text( "modulename, modulename... , list all modules with -l" )
      opt[Unit]( 'h', "help" ) action{ ( x, c )
        => c.copy( help = true ) } text( "Show help" )
      opt[String]( 'f', "filter" ) action { ( x, c )
        => c.copy( filter = x ) } text( "process filter" )
      /*
      opt[Unit]( 'e', "regex-filter" ) action{ ( x, c ) 
        => c.copy( regexFilter = true ) } text( "It is a regex filter type" ) */
    }

    val result = parser.parse( args.toSeq, Config( ) )

    if ( result.get.help )
      println( parser.usage )
    else
      sendMetrics( result )
  }
  
  private def sendMetrics( result: Option[ProcMetricsCouchDb.Config] ): Unit = {
    
    val timestamp = ( System.currentTimeMillis / 1000 ).toInt
    val docId = result.get.docNamePrefix + "_" + timestamp
    val docType = result.get.docType
    val url = result.get.couchdbUrl + "/" + result.get.database + "/" + docId 
    val filter: ( String, List[Pid] )  => List[Pid] = {
      if ( result.get.filter != "" )
        ProcFilter.patternFilter
      else
        ProcFilter.nullFilter
    }
    val data = Statistics.get(filter, result.get.filter ).toJson
    
    val jsonTransformer = ( __ ).json.update(
      __.read[JsObject].map { o => {
            var x = o ++ Json.obj( "_id" -> docId )
        	x = x ++ Json.obj( "docType" -> docType )
        	x ++ Json.obj( "runtime" -> timestamp ) } } )
    
    val transformed = data.transform( jsonTransformer )
    transformed.asOpt match {
      case Some( s ) => {
        val response = CouchDb.put( url, s.toString )
        if ( !response.success ) {
          println( "Error storing Document, code. " + response.code )
          if ( response.code < 0 )
            println( "Exception: " + response.message )
          else
            println( "CouchDb Message: " + response.body )
        } else {
          println( "Document successfully stored in CouchDb" )
        }
      }
      case _ => println( "Unknown error." )
    }
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
      val procInfo = new ProcInfo( "/" )
      val dirList = procInfo.getPidDirList()
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
      val x = r.filter( _._2.filter( _.values(0).asInstanceOf[ProcFloatValue].value != 0f ).length > 0 )
      x.foreach( pid => {
        val withoutNull = pid._2.filter( _.values(0).asInstanceOf[ProcFloatValue].value != 0f )
        if ( withoutNull.length > 0 ) {
          val t = withoutNull.map( x => x.name + ": " + x.values(0).asInstanceOf[ProcFloatValue].value )
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
