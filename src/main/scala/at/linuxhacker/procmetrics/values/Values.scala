package at.linuxhacker.procmetrics.values

case class Pid( pid: String, cmdline: String )

abstract class ProcGenValue
/*
case class ProcValueFloat( value: Float ) extends ProcGenValue
case class ProcValueInt( value: Int ) extends ProcGenValue
case class ProcValueString( value: String ) extends ProcGenValue
* 
*/
case class ProcValueX[T]( value: T ) extends ProcGenValue {
  def unapply[T]( x:ProcValueX[T] ) = Some( x.value )
}

case class ProcValue( name: String, value: ProcGenValue )
case class ProcCategory( pid: Pid, category: String, values: List[ProcValue] )
case class ProcGlobal( category: String, values: List[ProcValue] )