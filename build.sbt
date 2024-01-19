lazy val gojuon =
  (project in file("."))
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3")
    .settings(libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.2")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test)

lazy val console =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3")
    .dependsOn(core)

lazy val anki =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.16" % Test)
    .dependsOn(core)
