import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "de.martinpallmann",
      scalaVersion := "2.12.3",
      version      := "0.1.0"
    )),
    name := "uno",
    libraryDependencies ++= rootDependencies,
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
