package com.htmlism

import cats.effect._

object PrintRtkOrder extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    for {
      xs <- readFile("htk-hiragana.tsv")

      ys <- readFile("htk-katakana.tsv")

      _ <- IO.delay { xs.zip(Kana.allKanaMinusOld).foreach(println) }

      _ <- IO.delay { println; println; println }

      _ <- IO.delay { ys.zip(Kana.allKanaMinusOld).foreach(println) }
    } yield ExitCode.Success

  def readFile(s: String) =
    Resource
      .fromAutoCloseable(IO.delay(getClass.getResourceAsStream("/" + s)))
      .use(s => IO.delay {
        scala.io.Source.fromInputStream(s).getLines.toList
      })
      .map(_.tail)
      .map(_.flatMap { s =>
        val sixCols = s.split("\t")

        sixCols.toList.tail.filter(_.nonEmpty)
      })
}
