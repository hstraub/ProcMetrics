package at.linuxhacker.procmetrics.converts

import at.linuxhacker.procmetrics.values._

object ProcConverters {

  def toJson( data: List[List[ProcCategory]] ): String = {
    var cats = data.flatten.groupBy( _.pid.pid )
    "tester"
  }
}