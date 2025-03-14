val scala3Version = "3.6.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sceeper",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % Test,
    libraryDependencies += "org.scalatestplus" %% "scalacheck-1-16" % "3.2.12.0" % Test,
    libraryDependencies += "org.scalafx" %% "scalafx" % "18.0.2-R29"
  )
