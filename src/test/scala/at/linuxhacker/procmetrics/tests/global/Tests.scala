package at.linuxhacker.procmetrics.tests.global

import org.junit.runner.RunWith
import org.junit.Test
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.values._
import at.linuxhacker.procmetrics.lib.ProcInfo

@Test
@RunWith(classOf[JUnitRunner])
class Tests extends FlatSpec {

  private case class Testcase( content: List[String], f: Global, result: ProcGlobal )
  
  private val testset = "testfiles/set1/"
  private val procInfo = new ProcInfo( testset )
  
  private val results = List[Testcase](
      Testcase( procInfo.getFileContent( "cpuinfo" ),
            Cpuinfo,
            ProcGlobal( "cpuinfo", List( 
    		  ProcValue( "processor_count", ProcIntValue( 4 ) ),
    		  ProcValue( "model_name", ProcStringValue( "Intel(R) Core(TM) i5-2400 CPU @ 3.10GHz" ) ),
    		  ProcValue( "cpu_mhz", ProcFloatValue( 1600f ) ) ) )
      ),
      Testcase( procInfo.getFileContent( "uptime"),
            GlobalUptime,
      	    ProcGlobal( "uptime", List(
      	        ProcValue( "uptime_sec", ProcFloatValue( 9409.98f ) ) ) )
      ),
      Testcase( procInfo.getFileContent( "loadavg" ),
      Loadavg,
      ProcGlobal( "loadavg", List (
          ProcValue( "load_1m", ProcFloatValue( 0.55f ) ),
          ProcValue( "load_5m", ProcFloatValue( 0.26f ) ),
          ProcValue( "load_10m", ProcFloatValue( 0.20f ) ) ) )
      )
  )
  
  def extractValues ( stats: Option[ProcGlobal] ): List[ProcValue] = {
    stats match {
      case Some( g ) => g.values
      case _ => fail( "ProcGlobal does not returned stats" ); List[ProcValue]( )
    }
  }
  
  results.foreach( test => {
    
    test.result.category + " filename" must "be " + test.result.category in {
      assert( test.f.getFilename == test.result.category )
    }
    
    test.result.category + " ProcGlobal Category" must "be " + test.result.category in {
      val stats = test.f.getStat( test.content )
      val g = stats match {
        case Some( g ) => g
        case _ => fail( test.result.category + " does not returned stats" );
      }
      assert( g.category == test.result.category )
    }

    test.result.category + " ProcValues length" must "be " + test.result.values.length in {
      val values = extractValues( test.f.getStat( test.content ) )
      assert( values.length == test.result.values.length )
    }

    val values = extractValues( test.f.getStat( test.content ) )
    for ( ( x, i ) <- test.result.values.zipWithIndex ) {
      ( "ProcValue name " + i ) must ( "be " + x.name ) in {
        assert( values( i ).name == test.result.values( i ).name )
      }
    }
    for ( ( x, i ) <- test.result.values.zipWithIndex ) {
      ( "ProcValue value " + i ) must ( "be " + x.value.value ) in {
        assert( values( i ).value == test.result.values( i ).value )
      }
    }

    test.result.category + " Result" must "be equal" in {
      val stats = test.f.getStat( test.content )
      val g = stats match {
        case Some( g ) => g
        case _ => fail( test.result.category + " does not returned stats" );
      }
      assert( g == test.result )
    }
  
  } )

}