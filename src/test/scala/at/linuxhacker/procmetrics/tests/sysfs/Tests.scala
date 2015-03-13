package at.linuxhacker.procmetrics.tests.sysfs

import org.junit.runner.RunWith
import org.junit.Test
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import at.linuxhacker.procmetrics.lib.ProcInfo
import at.linuxhacker.procmetrics.sysfs._
import at.linuxhacker.procmetrics.values._


@Test
@RunWith(classOf[JUnitRunner])
class Tests extends FlatSpec {

  private case class NetDevice( name: String, mac: String )
  private val netDevices = List( NetDevice( "em1", "12:3f:a5:2f:b7:a3" ),
      NetDevice( "lo", "00:00:00:00:00:00" )  )
  private val testsets = List( "testfiles/set1/", "testfiles/set2/" )
  netDevices.foreach( x => execute( x ) )

  val procInfo = new ProcInfo( testsets(0) )
  val result2 = procInfo.getSysfsNetMac( { netDevices.map( x => x.name ) } )
  for ( ( c, i ) <- result2.zipWithIndex ) {
    checkResult( "getSysfsNetMac test nr " + i, netDevices( i ), c )
  }
  
  private def execute( netdevice: NetDevice ) = {

    val procInfo = new ProcInfo( testsets(0) )
    val filename = "sys/class/net/" + netdevice.name + "/address"

    "Device " + netdevice.name + " filename" must "be " + filename in  {
      assert( NetMac.getFilename( netdevice.name ) == filename )
    }

    val content = procInfo.getFileContent( NetMac.getFilename( netdevice.name ) )
    val result = NetMac.getStat( netdevice.name, content )
    result match {
      case Some( s ) => checkResult( "Direct test", netdevice, s )      
      case _ => fail( "NetMac.getStat returns None" )
    }
  }

  private def checkResult( testname: String, netdevice: NetDevice, result: ProcGlobal ): Unit = {
    testname + " device " + netdevice.name + " ProcGlobal values.length" must "be 1" in {
      assert( result.values.length == 1 )
    }

    testname + " device " + netdevice.name + " ProcGlobal value name" must "be mac" in {
      assert( result.values( 0 ).name == "mac" )
    }

    testname + " device " + netdevice.name must "have the mac Adress " + netdevice.mac in {
      assert( result.values( 0 ).values(0) == ValueFactory.create( netdevice.mac ) )
    }
  }
  
}