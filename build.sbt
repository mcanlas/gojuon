lazy val gojuon =
  (project in file("."))
    .settings(commonSettings: _*)
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.11")
    .settings(libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test)

lazy val console =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.11")
    .dependsOn(core)

lazy val anki =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.11")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test)
    .dependsOn(core)

lazy val commonSettings = List(scalaVersion := "2.13.8")

scalafmtOnCompile := true
