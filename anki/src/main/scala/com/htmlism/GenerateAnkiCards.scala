package com.htmlism

import cats.effect._
import cats.implicits._

object GenerateAnkiCards extends GenerateAnkiCards[IO] with IOApp {
  def toCard(uk: UnicodeKana): AnkiCard = {
    val romaji = Romaji.toRomaji(uk.kana)

    AnkiCard(romaji, uk.codePoint.toChar.toString, romaji)
  }
}

class GenerateAnkiCards[F[_]](implicit F: Sync[F]) {
  def run(args: List[String]): F[ExitCode] =
    for {
      dest <- getDest(args)
      _ <- writeFile(dest)
    } yield ExitCode.Success

  private def getDest(args: List[String]) =
    args match {
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
    }

  private def writeFile(dest: String) =
    FilePrinterAlg
      .resource[F](dest)
      .map(out => out.print _)
      .map(_.compose(Serialization.deckToString))
      .use(_(deck))

  private lazy val deck =
    Deck {
      Kana
        .buildUnicodeKana(Kana.hiraganaCodepoint)
        .map(GenerateAnkiCards.toCard)
    }
}