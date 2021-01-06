lazy val gojuon =
  (project in file("."))
    .settings(commonSettings: _*)
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.0.0-M5")
    .settings(libraryDependencies += "io.circe" %% "circe-yaml" % "0.12.0")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test)

lazy val console =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "mouse" % "0.25")
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.0.0-M5")
    .dependsOn(core)

lazy val anki =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "mouse" % "0.25")
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.0.0-M5")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test)
    .dependsOn(core)

lazy val commonSettings = List(scalaVersion := "2.13.4")

scalafmtOnCompile := true
