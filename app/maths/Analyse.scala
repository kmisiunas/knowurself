package maths

import com.github.nscala_time.time.Imports._


object Analyse {

  def basicAnalysis(user: String, question: Int): BasicStats = {
    val ts: List[(DateTime,Double)]  = connectivity.Database.getAnswers(user, question)
    val values = ts.map(_._2)
    val n = ts.size
    val sum = values.sum
    val mean: Double = sum/n
    val std2: Double = values.map(x => (x-mean)*(x-mean) ).sum /n
    val std = math.sqrt(std2)

    BasicStats(mean, std, n, 0, "", 0, "")
  }



}

case class BasicStats(mean: Double,
                      std: Double,
                      n: Int,
                      min:Double,
                      minDate: String,
                      max: Double,
                      maxDate: String )