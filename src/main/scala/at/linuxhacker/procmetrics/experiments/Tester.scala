
package at.linuxhacker.procmetrics.experiments

import scala.language.implicitConversions

trait Super {
  type A
  
  def value: A
}

case class B1( value: String ) extends Super {
  type A = String
}

case class B2( value: Float ) extends Super {
  type A = Float
}

/*
abstract class Super { } 

case class B1( value: String ) extends Super
case class B2( value: Float ) extends Super
case class T( value: String )
*/
// implicit def impConv( x: B2 ): B1 = B1( x.value.toString )

object Func {
  
  def f1( base: Super ) = {
    base match {
      case x: B1 => println( "B1: " + x.value )
      case x: B2 => println( "B2: " + x.value )
      case _ => throw new Exception( "Implementation error." )
    }
  }
  
  def f2( base: Super ) = {
    println( base.value )
  }
  
  def z1( base: B1 ) {
    println( "B1: " + base.value )
  }
  
  def z1( base: B2 ) {
    println( "B2: " + base.value )
  }
  
  def test( base: B2 ): B1 = {
    B1( base.value.toString )
  }
  
  def impl( base: B1 ) {
    z1( base )
  }
}

