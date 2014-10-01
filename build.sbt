version := "0.1-SNAPSHOT"

organization := "net.bmjames"

name := "scala-optparse-applicative"

scalaVersion in ThisBuild := "2.11.2"

crossScalaVersions := List("2.10.4", "2.11.2")

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)

scalacOptions ++= {
  if(scalaVersion.value.startsWith("2.11"))
    Seq("-Ywarn-unused", "-Ywarn-unused-import")
  else
    Nil
}

val scalazVersion = "7.1.0"

libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion

libraryDependencies += "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion

libraryDependencies += "com.googlecode.kiama" %% "kiama" % "1.7.0"
