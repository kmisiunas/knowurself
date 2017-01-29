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
    val tasks: List[Int] = connectivity.Database.getTasks(name.get)
    val noTasks: Int = tasks.size
    val questions: Map[Int,String] = connectivity.Database.getQuestions(name.get)

    val taskMap = tasks.map(id => (connectivity.Database.getTask(id), id)).toList
    //Ok(s"The user $name has $noTasks \n\nQuestions:\n\n" + questions.mkString("\n"))
    Ok(views.html.user(name.getOrElse("None"), questions.toList.map(_.swap), taskMap  ))
  }

  def question(name: Option[String], question: Option[Int]) = Action { implicit request =>
    Ok(views.html.question(
      username = name.getOrElse("None"),
      question = connectivity.Database.getQuestion(question.getOrElse(0)),
      questionNo = question.getOrElse(0),
      analysis = maths.Analyse.basicAnalysis(name.getOrElse("None"), question.getOrElse(0))
    ))
  }

  def task(name: Option[String], task: Option[Int]) = Action { implicit request =>
    val memory = maths.Cashed(name.getOrElse("None"), task.getOrElse(0))
    Ok(views.html.task(
      username = name.getOrElse("None"),
      task = connectivity.Database.getTask(task.getOrElse(0)),
      taskNo = task.getOrElse(0),
      cashedCorrelation = memory._1,
      recommendations = memory._2
    ))
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
    Ok(views.html.index(null))
  }



  def db = Action {
    Ok("users: " + connectivity.Database.getUsers().mkString(", ") )
  }
}
