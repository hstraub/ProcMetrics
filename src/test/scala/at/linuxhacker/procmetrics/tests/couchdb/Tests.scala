package at.linuxhacker.procmetrics.tests.couchdb
import org.junit.runner.RunWith
import org.junit.Test
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import at.linuxhacker.procmetrics.global._
import at.linuxhacker.procmetrics.values._
import at.linuxhacker.procmetrics.lib.ProcInfo
import at.linuxhacker.procmetrics.couchdb.CouchDb
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.scalatest.ConfigMap
import org.scalatest.BeforeAndAfterAllConfigMap
import org.junit.Ignore
import org.scalatest.Ignore

@org.scalatest.Ignore
@org.junit.Ignore
@Test
@RunWith(classOf[JUnitRunner])
class Tests extends FlatSpec with BeforeAndAfterAllConfigMap {

  val dbname = "stb_test"
  val docId = "eins"
  val name = "Herbert Straub"
  var hostname: String = "localhost"
  var urlPrefix: String = null
  var url: String = null

  private case class Configuration( urlPrefix: String, dbname: String, 
      docId: String, url: String, name: String )

  private var configuration: Configuration = null
  
  override def beforeAll(configMap: ConfigMap) {
    println( configMap )
    val hostname = configMap.getWithDefault( "couchdbhostname", "localhost" )
    urlPrefix = "http://" + hostname + ":5984/"
    url = urlPrefix  + dbname + "/" + docId
  }
  
  "Unknown host test" must "be failed" in {
    val response = CouchDb.get( "http://f.q.d.n:5984/dbname/docname" )
    assert( response.success == false )
  }
  
  "Get from Database " + dbname must "be success" in {
    val response = CouchDb.get( urlPrefix + dbname )
    assert( response.success )
  }

  "Store Document in database" must "be success" in {
    val data = Json.obj( "_id" -> docId, "name" -> name )
    val response = CouchDb.put( url , data.toString )
    assert( response.success )
  }

  "Get Document from database" must "be success and name must be " + name in {
    val response = CouchDb.get( url )
    if ( response.success != true ) fail( "Unable to retrieve document from CouchDb: " + response.code )
    val data = Json.parse( response.body )
    val responseName = data \ "name"
    assert( responseName.as[String] == name )
  }
  
  "Delete Test" must "be success" in {
    val getResponse = CouchDb.get( url )
    if ( getResponse.success != true ) fail( "Unable to retrieve Document from CouchDb" )
    val data = Json.parse( getResponse.body )
    val rev = data \ "_rev"
    val response = CouchDb.delete( url + "?rev=" + rev.as[String]  )
    assert( response.success )
  }

}