package at.linuxhacker.procmetrics.tests.pidstats

import org.junit.runner.RunWith
import org.junit.Test
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.values._
import at.linuxhacker.procmetrics.pidstats._
import at.linuxhacker.procmetrics.lib.ProcInfo

@Test
@RunWith(classOf[JUnitRunner])
class Tests extends FlatSpec {

  private case class Testcase( content: List[String], f:Stat, result: List[ProcCategory] )
  val testset = "testfiles/set1/"
    
  val pids = List( "1309", "952", "961", "963" )
  

}