package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

//import play.api.db._

import com.github.nscala_time.time.Imports._

import play.api.Logger

import java.sql._

object Application extends Controller {

  def user(name: Option[String]) = Action{implicit request =>
    val tasks = connectivity.Database.getTasks(name.get)
    val noTasks: Int = tasks.size
    val questions: Map[Int,String] = connectivity.Database.getQuestions(name.get)

    Ok(s"The user $name has $noTasks \n\nQuestions:\n\n" + questions.mkString("\n"))
  }

  // json generator
  def timeseries(user: Option[String], question: Option[Int]) = Action{implicit request =>
    if (user.isEmpty || question.isEmpty){
      Ok("Error: user and question ")
    } else {
      val data:List[(DateTime,Double)] = connectivity.Database.getAnswers(user.get, question.get)
      Ok( data.map(x => "["+ x._1.getMillis() +", "+ x._2 +"]").mkString("[", ",","]") )
    }
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

    Ok("users: " + connectivity.Database.getUsers().mkString(", ") )
  }
}
