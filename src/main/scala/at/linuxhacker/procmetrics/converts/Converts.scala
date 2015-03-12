package at.linuxhacker.procmetrics.converts

import at.linuxhacker.procmetrics.values._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import at.linuxhacker.procmetrics.global.MultiGlobalStatsResult

abstract class StatResult
case class StatGlobalResult( result: List[ProcGlobal] ) extends StatResult
case class StatStatsResult( result: List[ProcCategory] ) extends StatResult

object ProcConverters {

  def toJson( globals: List[ProcGlobal], stats: List[ProcCategory], 
		  multiGlobals: List[MultiGlobalStatsResult], sysStat: List[ProcGlobal] ): JsObject = {
    var pidGroup = stats.groupBy( _.pid.pid )
    
    val globalsList = globalsToJson( globals )

    val jsonStats = pidGroup.map( pid => {
      val catGroup = pid._2.groupBy( _.category )
      val x = catGroup.map( cat => {
        val valueList = cat._2.map( _.values  )
        valueList.map( x => Json.obj( cat._1 -> JsObject( x.map( procValueToJson( _ ) ).toSeq ) ) )
      }).flatten.toSeq
      Json.obj( pid._1 -> x )
    })
    
    val jsonMultiStats = multiGlobals.map( g => Json.obj( g.name -> JsObject( globalsToJson( g.result  ) ) ) )
    val jsonData = Json.obj( "globals" -> JsObject( globalsList ),
        "stats" -> jsonStats, "multi" -> jsonMultiStats,
        "sysfs" -> JsObject( globalsToJson( sysStat) ) )
    
    jsonData
  }
  
  def procCategoriesToJson( stats: List[ProcCategory2] ): Seq[(String, JsObject)] = {
    val pidGroups = stats.groupBy( _.pid.pid )
    val x = pidGroups.map( pidGroup => {
      val catList = pidGroup._2
      val catJson = catList.map( procCategory => procCategoryToJson( procCategory ) )
      ( pidGroup._1 -> catJson )
    }).toSeq
    Seq( )
  }

  def procCategoryToJson( procCategory: ProcCategory2 ): ( String, JsObject ) = {
    val valueList = JsObject( procCategory.keyValue.values.map( procValueToJson( _ ) ).toSeq )
    ( procCategory.keyValue.category -> valueList )
  }
  
  def globalsToJson( globals: List[ProcGlobal] ): Seq[(String,JsObject)] = {
    globals.map( item => {
      val valueList = JsObject( item.values.map( procValueToJson( _ ) ).toSeq )
      ( item.category -> valueList )
    }).toSeq
  }
  
  def procValueToJson( v: ProcValue): ( String, JsValue ) = {
    val x = v.value match {
      case a: ProcStringValue => JsString( a.value )
      case a: ProcIntValue => JsNumber( a.value )
      case a: ProcFloatValue => JsNumber( a.value )
    }
    ( v.name -> x )
  }
  
}