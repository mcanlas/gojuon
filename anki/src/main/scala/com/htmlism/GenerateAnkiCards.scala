package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.any._

object GenerateAnkiCards extends GenerateAnkiCards[IO] with IOApp {
  def toCard(script: String)(uk: UnicodeKana): AnkiCard = {
    val romaji = Romaji.toRomaji(uk.kana)

    AnkiCard(script + "-" + romaji, uk.codePoint.toChar.toString, romaji)
  }

  def toDeck(script: UnicodeKanaScript) =
    Deck {
      Kana
        .buildUnicodeKana(script.codePoint)
        .map(toCard(script.name))
    }
}

class GenerateAnkiCards[F[_]](implicit F: Sync[F]) {
  def run(args: List[String]): F[ExitCode] =
    for {
      baseDir <- getBaseDir(args)
      _ <- Kana.scripts.traverse(useScriptWriter(baseDir))
    } yield ExitCode.Success

  private def useScriptWriter(base: String)(script: UnicodeKanaScript) =
    FilePrinterAlg
      .resource[F](base + "/" + script.name + ".tsv")
      .use(writeScript(script))

  private def writeScript(script: UnicodeKanaScript)(out: FilePrinterAlg[F]) =
    script |>
      GenerateAnkiCards.toDeck |>
      Serialization.deckToString |>
      out.print

  private def getBaseDir(args: List[String]) =
    args match {
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
    }
}