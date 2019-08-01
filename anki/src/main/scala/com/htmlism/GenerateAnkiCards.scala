package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.any._

object GenerateAnkiCards extends GenerateAnkiCards[IO] with IOApp {
  def toCard(script: String)(uk: UnicodeKana): AnkiCard = {
    val romaji = Romaji.toRomaji(uk.kana)

    AnkiCard(script + "-" + romaji, uk.codePoint.toChar.toString, romaji)
  }
}

class GenerateAnkiCards[F[_]](implicit F: Sync[F]) {
  def run(args: List[String]): F[ExitCode] =
    for {
      baseDir <- getBaseDir(args)
      _ <- Kana.scripts.traverse(useScriptWriter(baseDir))
    } yield ExitCode.Success

  private def useScriptWriter(base: String)(script: (String, Int)) =
    FilePrinterAlg
      .resource[F](base + "/" + script._1 + ".tsv")
      .use(writeScript(script))

  private def writeScript(script: (String, Int))(out: FilePrinterAlg[F]) =
    script |>
      (toDeck _).tupled |>
      Serialization.deckToString |>
      out.print

  private def toDeck(script: String, codePoint: Int) =
    Deck {
      Kana
        .buildUnicodeKana(codePoint)
        .map(GenerateAnkiCards.toCard(script))
    }

  private def getBaseDir(args: List[String]) =
    args match {
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
    }
}