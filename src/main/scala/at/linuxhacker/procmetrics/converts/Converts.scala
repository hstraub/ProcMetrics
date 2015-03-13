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
    
    val jsonStats = procCategoriesToJson( stats )
    val globalsList = globalsToJson( globals )
    val jsonMultiStats = multiGlobals.map( g => Json.obj( g.name -> JsObject( globalsToJson( g.result  ) ) ) )
    val jsonData = Json.obj( "globals" -> JsObject( globalsList ),
        "stats" -> JsObject( jsonStats ), "multi" -> jsonMultiStats,
        "sysfs" -> JsObject( globalsToJson( sysStat) ) )
    
    jsonData
  }
  
  def procCategoriesToJson( stats: List[ProcCategory] ): Seq[(String, JsObject)] = {
    val pidGroups = stats.groupBy( _.pid.pid )
    val x = pidGroups.map( pidGroup => {
      val catList = pidGroup._2
      val catJson = catList.map( procCategory => 
        procCategoryToJson( procCategory ) ).toSeq
      ( pidGroup._1 -> JsObject( catJson ) )
    }).toSeq
    x
  }

  def procCategoryToJson( procCategory: ProcCategory ): ( String, JsObject ) = {
    val valueList = JsObject( procCategory.values.map( procValueToJson( _ ) ).toSeq )
    ( procCategory.category -> valueList )
  }
  
  def globalsToJson( globals: List[ProcGlobal] ): Seq[(String,JsObject)] = {
    globals.map( item => {
      val valueList = JsObject( item.values.map( procValueToJson( _ ) ).toSeq )
      ( item.category -> valueList )
    }).toSeq
  }
  
  def procValueToJson( v: ProcValue): ( String, JsValue ) = {
    val jsonValues = v.values.map( singleValue => {
      val singleJsonValue = singleValue match {
        case a: ProcStringValue => JsString( a.value )
        case a: ProcIntValue    => JsNumber( a.value )
        case a: ProcFloatValue  => JsNumber( a.value )
      }
      singleJsonValue
    } )
    
    if ( jsonValues.length > 1 )
      ( v.name -> JsArray( jsonValues ) )
    else
      ( v.name -> jsonValues(0) )
 }
  
}