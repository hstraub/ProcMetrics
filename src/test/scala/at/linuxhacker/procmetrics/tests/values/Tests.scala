package at.linuxhacker.procmetrics.tests.values

import org.junit.runner.RunWith
import org.junit.Test
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import at.linuxhacker.procmetrics.values._

@Test
@RunWith(classOf[JUnitRunner])
class Tests extends FlatSpec {

  "An ProcStringValue 'tester'" must "have the value 'tester'" in {
    assert( ProcStringValue( "tester" ).value == "tester" )
  }
  
  "ProcIntValue 4" must "have the value 4" in {
    assert( ProcIntValue( 4 ).value == 4 )
  }
  
  "ProcFloatValue 3.1415" must "have the value 3.1415 " in {
    assert( ProcFloatValue( 3.1415f ).value == 3.1415f )
  }
  
  "Implicit Conversion Int2Float" must "have result 4f" in {
    import ValueConverters._
    def test( x: ProcFloatValue ) = x.value
    val intValue = ProcIntValue( 4 )
    val what = test( intValue )
    assert( what == 4f )
  }
  
  "Implicit Conversion Float2String" must "be '3.1415'" in {
    import ValueConverters._
    def test( x: ProcStringValue ): Boolean = x.value == "3.1415"
    def f = ProcFloatValue( 3.1415f )
    val x = test( f )
    assert( x == true )
  }
}