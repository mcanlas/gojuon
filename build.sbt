lazy val gojuon =
  (project in file("."))
    .aggregate(core, console, anki)

lazy val core =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.14")
    .settings(libraryDependencies += "io.circe" %% "circe-yaml" % "0.14.1")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test)

lazy val console =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.14")
    .dependsOn(core)

lazy val anki =
  project
    .settings(libraryDependencies += "org.typelevel" %% "cats-effect" % "3.3.14")
    .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test)
    .dependsOn(core)
