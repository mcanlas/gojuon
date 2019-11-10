lazy val gojuon =
  (project in file("."))
    .settings(commonSettings: _*)
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0")

lazy val console =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "mouse" % "0.23")
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0")
    .dependsOn(core)

lazy val anki =
  project
    .settings(commonSettings: _*)
    .settings(libraryDependencies += "org.typelevel" %% "mouse" % "0.23")
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "2.0.0")
    .dependsOn(core)

lazy val commonSettings = List(
  scalaVersion := "2.13.1")

scalafmtOnCompile := true
