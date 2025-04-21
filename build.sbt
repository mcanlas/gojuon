lazy val gojuon =
  (project in file("."))
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6.1")
    .settings(libraryDependencies += "io.circe" %% "circe-yaml" % "0.15.1")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test)

lazy val console =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6.1")
    .dependsOn(core)

lazy val anki =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6.1")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test)
    .dependsOn(core)
