package at.linuxhacker.procmetrics.values

case class Pid( pid: String, cmdline: String )

trait ProcGenValue {
  type A
  
  def value: A
}

case class ProcStringValue( value: String ) extends ProcGenValue { type A = String }
case class ProcFloatValue( value: Float ) extends ProcGenValue { type A = Float }
case class ProcIntValue( value: Int ) extends ProcGenValue { type A = Int }

case class ProcValue( name: String, value: ProcGenValue )
case class ProcCategory( pid: Pid, category: String, values: List[ProcValue] )
case class ProcGlobal( category: String, values: List[ProcValue] )