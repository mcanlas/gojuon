package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.any._

object Gojuon extends IOApp {
  val hiraganaCodepoint = 0x3041
  val katakanaCodepoint = 0x30A1

  def run(args: List[String]): IO[ExitCode] =
    IO {
      for (n <- 0 to 100) {
        (hiraganaCodepoint + n).toChar |> println
      }

      println
      println
      println

      for (n <- 0 to 100) {
        (katakanaCodepoint + n).toChar |> println
      }
    }.as(ExitCode.Success)
}

