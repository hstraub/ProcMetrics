package at.linuxhacker.procmetrics.sysfs

import at.linuxhacker.procmetrics.values._

abstract class SysStat {
  def getFilename( deviceName: String ): String
  def getStat( deviceName: String, content: List[String] ): Option[ProcGlobal]
}

object NetMac extends SysStat {
  
  val macPattern = """([0-9a-f]{2}:[0-9a-f]{2}:[0-9a-f]{2}:[0-9a-f]{2}:[0-9a-f]{2}:[0-9a-f]{2})""".r
  
  def getFilename( netDeviceName: String ): String = { 
    "sys/class/net/" + netDeviceName + "/address"
  }
  
  def getStat( netDeviceName: String, content: List[String] ): Option[ProcGlobal] = {
    content(0) match {
      case macPattern( mac ) => 
        Some( ProcGlobal( netDeviceName, List( ProcValueFactory.create( "mac", ValueFactory.create( mac ) ) ) ) )
      case _ => None
    }
  }

}