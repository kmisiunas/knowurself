name := """KnowUrSelf"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  ws
)

// does not load?!
//libraryDependencies += "com.microsoft.sqlserver" % "sqljdbc4" % "4.0"

libraryDependencies += "commons-dbutils" % "commons-dbutils" % "1.6"


libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )
