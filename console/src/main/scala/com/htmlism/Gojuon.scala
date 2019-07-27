package com.htmlism

import cats.effect._
import cats.implicits._

object Gojuon extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    IO {
      Kana.buildUnicodeKana(Kana.hiraganaCodepoint, Kana.kanaVariants, Nil)
        .foreach(u => println(u.codePoint.toChar + " " + u.toString))

      Kana.buildUnicodeKana(Kana.katakanaCodepoint, Kana.kanaVariants, Nil)
        .foreach(u => println(u.codePoint.toChar + " " + u.toString))
    }.as(ExitCode.Success)
}