package at.linuxhacker.procmetrics.converts

import at.linuxhacker.procmetrics.values._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object ProcConverters {

  def toJson( globals: List[ProcGlobal], stats: List[List[ProcCategory]] ): String = {
    var pidGroup = stats.flatten.groupBy( _.pid.pid )
    
    val globalsList = globals.map( item => {
      val valueList = item.values.map( procValueToJson( _ ) )
      Json.obj( item.category -> valueList )
    })

    val jsonStats = pidGroup.map( pid => {
      val catGroup = pid._2.groupBy( _.category )
      val x = catGroup.map( cat => {
        val valueList = cat._2.map( _.values  )
        valueList.map( x => Json.obj( cat._1 -> x.map( procValueToJson( _ ) ) ) )
      })
      Json.obj( pid._1 -> x )
    })
    
    val jsonData = Json.obj( "globals" -> globalsList, "stats" -> jsonStats )
    
    
    jsonData.toString
  }
  
  private def procValueToJson( v: ProcValue): JsObject = {
        val x = v.value match {
          case ProcValueX( a: String ) => JsString( a  )
          case ProcValueX( a: Int ) => JsNumber( a  )
          case ProcValueX( a: Float ) => JsNumber( a  )
        }
        Json.obj( v.name  -> x )
  }
}