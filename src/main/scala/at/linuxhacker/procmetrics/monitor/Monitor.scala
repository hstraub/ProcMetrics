package at.linuxhacker.procmetrics.monitor

import at.linuxhacker.procmetrics.values._

case class Column( category: String, name: String )

object MonitorFunctions {
  def transformToColumns( stats: List[List[ProcCategory]], columns: List[Column] ) = {
    val t = stats.flatten
    val categories = columns.map( _.category ).toSet
    val names = columns.map( x => x.category -> x.name  ).toMap
    val paths = columns.map( x => x.category + "/" + x.name )
	val filteredCats = t.filter( cat => categories contains cat.category )
	val filteredStats = filteredCats.map( x => {
	  val filteredValues = x.values.filter( v => ( paths contains x.category + "/" + v.name  )
	      && ( v.value match { case ProcValueX( x: Int) => true case ProcValueX( x: Float ) => true case _ => false } ) )
	  ProcCategory(  x.pid, x.category, filteredValues )
	})
	
	var pids = scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[ProcValue]]( )
	filteredStats.foreach( x => {
	  if ( ! pids.contains( x.pid.pid ) )
	    pids += x.pid.pid -> scala.collection.mutable.ListBuffer[ProcValue]( )
	  x.values.foreach( v => {
	    pids(x.pid.pid) += ProcValue( x.category + "/" + v.name, v.value ) 
	  })
	})

	val table: scala.collection.Map[String, List[ProcValue]] = pids.mapValues( v => v.toList )
	table
  }
}