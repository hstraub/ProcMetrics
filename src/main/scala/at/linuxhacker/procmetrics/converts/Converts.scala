package at.linuxhacker.procmetrics.converts

import at.linuxhacker.procmetrics.values._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import at.linuxhacker.procmetrics.global.MultiGlobalStatsResult

abstract class StatResult
case class StatGlobalResult( result: List[ProcGlobal] ) extends StatResult
case class StatStatsResult( result: List[ProcCategory] ) extends StatResult

object ProcConverters {

  def toJson( globals: List[ProcGlobal], stats: List[ProcCategory], multiGlobals: List[MultiGlobalStatsResult] ): String = {
    var pidGroup = stats.groupBy( _.pid.pid )
    
    val globalsList = globalsToJson( globals )

    val jsonStats = pidGroup.map( pid => {
      val catGroup = pid._2.groupBy( _.category )
      val x = catGroup.map( cat => {
        val valueList = cat._2.map( _.values  )
        valueList.map( x => Json.obj( cat._1 -> x.map( procValueToJson( _ ) ) ) )
      })
      Json.obj( pid._1 -> x )
    })
    
    val jsonMultiStats = multiGlobals.map( g => Json.obj( g.name -> globalsToJson( g.result  ) ) )
    
    val jsonData = Json.obj( "globals" -> globalsList, "stats" -> jsonStats, "multi" -> jsonMultiStats)
    
    
    jsonData.toString
  }
  
  private def globalsToJson( globals:  List[ProcGlobal] ): List[JsObject] = {
    globals.map( item => {
      val valueList = item.values.map( procValueToJson( _ ) )
      Json.obj( item.category -> valueList )
    })
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