lazy val gojuon =
  (project in file("."))
    .settings(commonSettings: _*)
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.4")
    .settings(libraryDependencies += "io.circe" %% "circe-yaml" % "0.12.0")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test)

lazy val console =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "mouse" % "0.25")
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.4")
    .dependsOn(core)

lazy val anki =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "mouse" % "0.25")
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "2.1.4")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test)
    .dependsOn(core)

lazy val commonSettings = List(scalaVersion := "2.13.3")

scalafmtOnCompile := true
