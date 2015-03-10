package at.linuxhacker.procmetrics.tests.lib

import org.junit.runner.RunWith
import org.junit.Test
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.values._
import at.linuxhacker.procmetrics.lib._
import at.linuxhacker.procmetrics.pidstats.Io

@Test
@RunWith(classOf[JUnitRunner])
class Tests extends FlatSpec {
  
  private case class PidData( pid: String, cmdline: String )
  
  val targetCategories = List[ProcCategory](
    ProcCategory( Pid( "963", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 1024.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 0.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 3.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 0.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "952", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 75182.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 260.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 39.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 4.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 3719168.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "952", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 75182.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 260.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 39.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 4.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 3719168.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "961", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 1024.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 0.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 3.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 0.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "1309", "/usr/bin/kactivitymanagerd " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 722539.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 282820.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 1309.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 1755.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 1859584.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 147456.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) )
  )
  
  val targetCategories2 = List[ProcCategory](
    ProcCategory( Pid( "963", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 1024.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 0.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 3.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 0.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "952", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 75182.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 260.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 39.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 4.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 3719168.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "952", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 75182.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 260.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 39.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 4.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 3719168.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "961", "/usr/sbin/httpd -k start " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 1024.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 0.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 3.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 0.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 0.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) ),
    ProcCategory( Pid( "1309", "/usr/bin/kactivitymanagerd " ), "io", List[ProcValue](
      ProcValue( "rchar", ProcFloatValue( 722555.0f ) ),
      ProcValue( "wchar", ProcFloatValue( 283004.0f ) ),
      ProcValue( "syscr", ProcFloatValue( 1311.0f ) ),
      ProcValue( "syscw", ProcFloatValue( 1764.0f ) ),
      ProcValue( "read_bytes", ProcFloatValue( 1859584.0f ) ),
      ProcValue( "write_bytes", ProcFloatValue( 147456.0f ) ),
      ProcValue( "cancelled_write_bytes", ProcFloatValue(0.0f) ) ) )
  )
  
  doTestsForTestset( "testfiles/set1/", targetCategories )
  doTestsForTestset( "testfiles/set2/", targetCategories2 )

  def doTestsForTestset( testset: String, targetCategories: List[ProcCategory] ) = {

    val procInfo = new ProcInfo( testset )
    val pids = procInfo.getDirList
    val pidMap = targetCategories.map( x => { x.pid.pid -> x.pid.cmdline } ).toMap

    "PID list in " + testset must "have length " + pidMap.size in {
      assert( pids.length == 4 )
    }

    pids.foreach( pid => {
      testset + " " + pid must "be in pid set" in {
        assert( pidMap.contains( pid ) == true )
      }
    } )

    val pidList = procInfo.getCommandList( pids )
    pidList.foreach( x => {
      val desiredCommandline = pidMap.getOrElse( x.pid, "?" )
      testset + " " + "Pid: " + x.pid must "have commandline: " + desiredCommandline in {
        assert( x.cmdline == desiredCommandline )
      }
    } )

    val filteredPids1 = ProcFilter.nullFilter( "", pidList )
    testset + " " + "ProcFilter nullFilter" must "have length " + pidMap.size in {
      assert( filteredPids1.length == pidMap.size )
    }

    val httpdFilteredPids = ProcFilter.patternFilter( "httpd", pidList )
    testset + " " + "ProcFilter patternFilter httpd" must "have length 3" in {
      assert( httpdFilteredPids.length == 3 )
    }

    val stats = procInfo.getStat( List( Io ), pidList )
    stats.foreach( stat => {
      val target = targetCategories.filter( x => stat.pid.pid == x.pid.pid )( 0 )
      testset + " " + "ProcCategory from pid: " + stat.pid.pid must "be equal with target" in {
        assert( stat == target )
      }
    } )

  }

}