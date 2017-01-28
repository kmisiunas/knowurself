package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current
import play.api.Logger

import java.sql._
import com.microsoft.sqlserver.jdbc._

import play.api.db._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(null))
  }


  def db2 = Action {

    val connectionString = "jdbc:sqlserver://knowyourself.database.windows.net:1433;database=KnowYourself;user=su@knowyourself;password=!alexandrU97;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";

    var connection: Connection = null;

    try {
      connection = DriverManager.getConnection(connectionString);

    }
    catch {
        case e: Exception => println(e)
    }
    finally {
      if (connection != null)  connection.close();
    }

    Ok("ok!")
  }


  def db = Action {
    var out = ""
    val conn = DB.getConnection()
    /*
    jdbc:sqlserver://knowyourself.database.windows.net:1433;database=KnowYourself;user=su@knowyourself;password=!alexandrU97;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
     */
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      val rs = stmt.executeQuery("SELECT tick FROM ticks")

      while (rs.next) {
        out += "Read from DB: " + rs.getTimestamp("tick") + "\n"
      }
    } finally {
      conn.close()
    }
    Ok(out)
  }
}
