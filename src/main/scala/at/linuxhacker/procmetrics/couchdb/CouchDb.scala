package at.linuxhacker.procmetrics.couchdb

import scalaj.http._
import java.net.UnknownHostException

case class Response( code: Int, success: Boolean, message: String,
    body: String, headers: Map[String, String] )

object CouchDb {
  def get( url: String ): Response = {
    execute( Http( url ) )
  }
  
  def put( url: String, jsonData: String ): Response = {
    execute( Http( url )
      .header( "content-type", "application/json" )
      .postData( jsonData )
      .method( "PUT" ) )
  }
  
  def delete( url: String ): Response = {
    execute( Http( url ).method( "DELETE" ) )
  }
  
  private def execute( request: HttpRequest ): Response = {
    try {
      val response: HttpResponse[String] = request.asString
      Response( response.code, response.isSuccess, response.statusLine, response.body, response.headers )
    } catch {
      case e: UnknownHostException => Response( -1, false, "Unknown host: " + e.getMessage(), "", Map() )
      case e: Exception => Response( -2, false, e.getMessage(), "", Map() )
    }
  }
}