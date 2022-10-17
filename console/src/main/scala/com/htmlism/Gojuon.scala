package com.htmlism

import cats.effect._
import cats.syntax.all._

object Gojuon extends IOApp.Simple:
  def run: IO[Unit] =
    Kana
      .scripts
      .map(_.codePoint)
      .traverse { cp =>
        IO {
          Kana
            .buildUnicodeKana(cp)
            .foreach(u => println(u.codePoint.toChar.toString + " " + u.toString))
        }
      }
      .void
