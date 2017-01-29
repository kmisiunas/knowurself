package connectivity

import java.sql._

import com.github.nscala_time.time.Imports._


object Database {

  val tables = Map(
    "users" -> List("id", "slack_id"),
    "answers" ->  List("id", "question_id", "answer", "user_id", "date"),
    "diseases" -> List("id", "disease_name"),
    "questions" -> List("id","question"),
    "disease_questions" -> List("id", "question_id", "disease_id"),
    "recs" -> List("id", "user_id", "disease_id", "rec"),
    "user_diseases" -> List("id", "user_id", "disease_id")
  )


  def realize(queryResult: ResultSet): Vector[Map[String, Object]] = {
    val md = queryResult.getMetaData
    val colNames = for (i <- 1 to md.getColumnCount) yield md.getColumnName(i)
    val buildMap = () => (for (n <- colNames) yield n -> queryResult.getObject(n)).toMap
    Iterator.continually(queryResult.next()).takeWhile(identity).map(_ => buildMap()).toVector
  }

  /**  connect to DB */
  private def getConnection(): Connection =  {
    val connection = (new connect.Connect()).getConnection
    if(connection == null) throw new Exception("Could not connect to the server")
    else return connection
  }

 // Get Methods


  def getUsers(): Map[Int, String] = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val resultSet = statement.executeQuery("SELECT * FROM users")
    val data = realize(resultSet)
    statement.close()
    connection.close()
    data.map(x => x("id").asInstanceOf[Int] -> x("slack_id").toString).toMap
  }

  def getUserId(user: String, statement: Statement): Int = {
    val resultSet = statement.executeQuery("SELECT * FROM users")
    val data = realize(resultSet)
    val users = data.map(x =>  x("slack_id").toString -> x("id").asInstanceOf[Int] ).toMap
    if( users.contains(user)) users(user) else -1
  }

  def getAnswers(user: String, question: Int): List[(DateTime,Double)] = {
    val connection = getConnection()
    val statement = connection.createStatement()
    // get user id
    val userId = getUserId(user, statement)
    // get info
    val resultSet2 = statement.executeQuery(
      s"SELECT date,answer FROM answers WHERE user_id=$userId AND question_id=$question"
    )
    val data2 = realize(resultSet2)
    statement.close()
    connection.close()
    data2.map(x => DateTime.parse(x("date").toString) -> x("answer").toString.toDouble).toList
  }

  def getTasks(user: String, statement: Statement): List[Int] = {
    val userId = getUserId(user, statement)
    // get tasks
    val resultSet = statement.executeQuery(
      s"SELECT disease_id FROM user_diseases WHERE user_id=$userId"
    )
    realize(resultSet).map(_("disease_id").toString.toInt).toList
  }
  def getTasks(user: String): List[Int] = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val res = getTasks(user, statement)
    statement.close()
    connection.close()
    res
  }


  def getQuestions(user: String, statement: Statement): Map[Int,String] = {
    // get user id
    val tasks = getTasks(user, statement).toSet
    // get all questions
    val resultSet = statement.executeQuery(
      s"SELECT question_id, disease_id FROM disease_questions"
    )
    val allTasksAndQuestions = realize(resultSet)
    val allQuestions = allTasksAndQuestions
      .filter(x=> tasks.contains( x("disease_id").toString.toInt ) )
      .map(x => x("question_id").toString.toInt )
    allQuestions.map(id => id -> getQuestion(id, statement)).toMap
  }
  def getQuestions(user: String): Map[Int,String] = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val res = getQuestions(user, statement)
    statement.close()
    connection.close()
    res
  }

  def getQuestion(questionId: Int, statement: Statement): String = {
    val resultSet = statement.executeQuery(
      s"SELECT question FROM questions WHERE id=$questionId"
    )
    realize(resultSet).map(_("question").toString).head
  }
  def getQuestion(questionId: Int): String = {
    val connection =  getConnection()
    val statement = connection.createStatement()
    val res = getQuestion(questionId, statement)
    statement.close(); connection.close()
    res
  }


  // Add Methods


  def addUser(name: String): Unit = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val resultSet = statement.execute(s"INSERT INTO users (slack_id) VALUES ('$name')")
    statement.close()
    connection.close()
  }

  def addQuestion(name: String): Unit = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val resultSet = statement.execute(s"INSERT INTO questions (question) VALUES ('$name')")
    statement.close()
    connection.close()
  }

  def addAnswer(question: Int, user: String, value: Double, date: String): Unit = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val userId = getUserId(user, statement)
    statement.execute(s"INSERT INTO answers (question_id, user_id, answer, date) VALUES ($question,$userId,$value, '$date')")
    statement.close()
    connection.close()
  }

  def addAnswer(question: Int, user: String, list: List[(String,Double)]): Unit = {
    val connection = getConnection()
    val statement = connection.createStatement()
    val userId = getUserId(user, statement)
    list.foreach(x => {
      val date = x._1;
      val value = x._2;
      statement.execute(
        s"INSERT INTO answers (question_id, user_id, answer, date) VALUES ($question,$userId,$value, '$date')")
    })
    statement.close()
    connection.close()
  }


}