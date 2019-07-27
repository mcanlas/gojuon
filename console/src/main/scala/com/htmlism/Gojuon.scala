package com.htmlism

import cats.effect._
import cats.implicits._

object Gojuon extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    IO {
      println(s"number of kana variants is ${Kana.kanaUnicodeDescriptions.size}")

      for (n <- Kana.kanaUnicodeDescriptions.indices) {
        println {
          Kana.kanaUnicodeDescriptions(n) + ": " + (Kana.hiraganaCodepoint + n).toChar
        }
      }

      println
      println
      println

      for (n <- Kana.kanaUnicodeDescriptions.indices) {
        println {
          Kana.kanaUnicodeDescriptions(n) + ": " + (Kana.katakanaCodepoint + n).toChar
        }
      }

    Kana.buildUnicodeKana(Kana.hiraganaCodepoint, Kana.kanaVariants, Nil)
      .foreach(u => println(u.codePoint.toChar + " " + u.toString))

    }.as(ExitCode.Success)
}