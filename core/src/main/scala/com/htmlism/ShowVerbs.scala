package com.htmlism

import cats.effect._

object ShowVerbs extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    DataLoader
      .parseEntries[IO]("/verbs-ru.yaml")
      .map { xs =>
        xs.foreach { x =>
          println(x)

          val stem =
            ParseVerbs.toStem(x.japanese.s)

          ParseVerbs
            .toVerbForms(stem)
            .foreach(println)
        }
      }
      .as(ExitCode.Success)
}

object ParseVerbs {
  val ru     = "\u308B"
  val imas   = "ます"
  val imasen = "ません"

  def toStem(s: String): String = {
    assert(s.substring(s.length - 1, s.length) == ru)

    s.substring(s.length - 1)
  }

  def toVerbForms(s: String): List[String] =
    for {
      suf <- List(imas, imasen)
    } yield s + suf
}
