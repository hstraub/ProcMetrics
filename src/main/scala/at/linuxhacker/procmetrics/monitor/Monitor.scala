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
      val filteredValues = x.values.filter( v => ( paths contains x.category + "/" + v.name )
        && ( v.value match { case ProcValueX( x: Int ) => true case ProcValueX( x: Float ) => true case _ => false } ) )
      val floatValues = filteredValues.map( v => {
        v.value match {
          case ProcValueX( x: Int ) => ProcValue( v.name, ProcValueX[Float]( x.toFloat ) )
          case ProcValueX( x: Float ) => v
          case _ => null
        }
      } )
      ProcCategory( x.pid, x.category, floatValues )
    } )
	
	var pids = scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[ProcValue]]( )
	filteredStats.foreach( x => {
	  if ( ! pids.contains( x.pid.pid ) )
	    pids += x.pid.pid -> scala.collection.mutable.ListBuffer[ProcValue]( )
	  x.values.foreach( v => {
	    pids(x.pid.pid) += ProcValue( x.category + "/" + v.name, v.value ) 
	  })
	})

	val result: scala.collection.immutable.Map[String, List[ProcValue]] = pids.keys.map( k => k -> pids(k).toList ).toMap
	result
  }
  
  def diffTables( t1: Map[String, List[ProcValue]],
		  t2: Map[String, List[ProcValue]] ): scala.collection.immutable.Map[String, List[ProcValue]] = {
    
    val s1 = t1.map( x => x._1 ).toSet
    val s2 = t2.map( x => x._1 ).toSet
    val commons = s1.intersect( s2 )

    var temp = scala.collection.mutable.Map[String, scala.collection.mutable.ListBuffer[ProcValue]]( )
    commons.foreach( pid => {
      var diffList = scala.collection.mutable.ListBuffer[ProcValue]( )
      for ( ( l1, i ) <- t1(pid).zipWithIndex ) {
        val x = t1(pid)(i).value
        val y = x.asInstanceOf[ProcValueX[Float]]
        if ( t1(pid).length == t2(pid).length ) {
        val value1 = t1(pid)(i).value.asInstanceOf[ProcValueX[Float]].value
        val value2 = t2(pid)(i).value.asInstanceOf[ProcValueX[Float]].value
        diffList += ProcValue( t1(pid)(i).name, ProcValueX[Float]( value2 - value1 )  )
        } else {
          println( "Unterschied: +++++++++++++++++++++++" )
          println( t1(pid) )
          println( "-------" )
          println( t2(pid) )
          println( "Ende: ++++++++++++++++++++++++++++++" )
        }
      }
      temp += pid -> diffList
    })
    
    
    val result: scala.collection.immutable.Map[String, List[ProcValue]] = temp.keys.map( k => k -> temp(k).toList ).toMap
	result
  }
}