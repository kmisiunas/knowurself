package maths

object Cashed {

  // get it from Mathematica
  private val map: Map[(String, Int), (String, List[String])] = Map(
    ("kmisiunas", 1) -> ("kmisiunas-1.png",
    List(
      "Coffee increases your daily energy",
      "Alchol decreases your daily energy"
    ))
  )

  def apply(name: String, task: Int): (String, List[String]) = if(!map.contains((name, task))) {
    (null, List("Need to collect more data, before we can reach conclusions"))
  } else {
    map((name, task))
  }

}