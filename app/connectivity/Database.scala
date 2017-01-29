package connectivity

import java.sql._


object Database {

  def getUsers(): Option[List[String]] = {
    val c = new connect.Connect()
    val connection = c.getConnection;
    //Logger.debug(connection.toString)

    if (connection == null) {

      Some(List("Failed"))
    }
    else {

      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT * FROM users")
      val users = (new Iterator[String] {
        def hasNext = resultSet.next()

        def next() = resultSet.getString(1)
      }).toStream.toList
      connection.close()
      Some(users.toList)
    }


  }

}