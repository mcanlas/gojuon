package com.htmlism

import cats.effect._
import cats.implicits._

object Gojuon extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Kana
      .scripts
      .map(_.codePoint)
      .traverse { cp =>
        IO {
          Kana
            .buildUnicodeKana(cp)
            .foreach(u => println(u.codePoint.toChar + " " + u.toString))
        }
      }.as(ExitCode.Success)
}