package at.linuxhacker.procmetrics.values

import scala.language.implicitConversions

case class Pid( pid: String, cmdline: String )

trait ProcGenValue {
  type A
  
  def value: A
}

case class ProcStringValue( value: String ) extends ProcGenValue { type A = String }
case class ProcFloatValue( value: Float ) extends ProcGenValue { type A = Float }
case class ProcIntValue( value: Int ) extends ProcGenValue { type A = Int }

object ValueConverters {
  implicit def f2s( x: ProcFloatValue ): ProcStringValue = ProcStringValue( x.value.toString )
  implicit def i2s( x: ProcIntValue ): ProcStringValue = ProcStringValue( x.value.toString )
  implicit def i2f( x: ProcIntValue ): ProcFloatValue = ProcFloatValue( x.value )
}

object ValueFactory {
  def create( x: String ): ProcGenValue = { ProcStringValue( x )  }
  def create( x: Float ): ProcGenValue = { ProcFloatValue( x ) }
  def create( x: Int ): ProcGenValue = { ProcIntValue( x ) }
}

case class ProcValue( name: String, value: ProcGenValue )
case class ProcCategory( pid: Pid, category: String, values: List[ProcValue] )
case class ProcGlobal( category: String, values: List[ProcValue] )