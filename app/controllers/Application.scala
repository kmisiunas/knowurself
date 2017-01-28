package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

import play.api.db._

import play.api.Logger

//import java.sql._
//import com.microsoft.sqlserver.jdbc._

object Application extends Controller {

  def user(name: Option[String]) = Action{implicit request =>
    Ok(s"Name is: $name")

  }

  def timeseries(user: Option[String], data: Option[String]) = Action{implicit request =>


    Ok(s"User is: $user and the data is $data")
  }


  def index = Action {
    Logger.debug("test debug")
    Logger.error("test error")
    Ok(views.html.index(null))
  }


  def db2 = Action {

    val c = new connect.Connect()
    val connection = c.getConnection;
    Logger.debug(connection.toString)

    if (connection == null){
      Logger.debug("connection is null")
      Ok("FAILED: no connection to database")
    }
    else {

      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT * FROM users")
      val users = new Iterator[String] {
        def hasNext = resultSet.next()
        def next() = resultSet.getString(1)
      }.toStream
      connection.close()
      Ok("all users: " + users.mkString(", ") )
    }
//    val connectionString = "jdbc:sqlserver://knowyourself.database.windows.net:1433;database=KnowYourself;user=su@knowyourself;password=!alexandrU97;encrypt=false;trustServerCertificate=true;hostNameInCertificate=*.database.windows.net;loginTimeout=90;";
//    val user = "su"
//    val pass = "!alexandrU97"
//
//    var connection: Connection = null;
//
//    try {
//      connection = DriverManager.getConnection(connectionString, user, pass);
//
//    }
//    catch {
//        case e: Exception => Logger.error(e.getMessage)
//    }
//    if (connection == null){
//      Logger.debug("connection is null")
//    }
    //val selectSql = "SELECT * FROM users"
    //val statement = connection.createStatement()
    //Logger.debug(statement.toString)
    //val resultSet = statement.executeQuery(selectSql);
    //resultSet.getString(1)
    //connection.close()
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
