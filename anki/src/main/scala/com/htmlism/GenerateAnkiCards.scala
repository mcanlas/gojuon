package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.any._

object GenerateAnkiCards extends GenerateAnkiCards[IO] with IOApp {
  private def toCard(script: String)(uk: UnicodeKana): AnkiCard = {
    val romaji = Romaji.toRomaji(uk.variant)

    val cardId = script + "-" + Romaji.toKanaId(uk.variant)

    val front = s"""<div id="japanese-heroic-character"><span class="$script">${uk.codePoint.toChar.toString}</span></div>"""

    val back = s"""<div id="japanese-romaji-answer">$romaji</div>"""

    AnkiCard(cardId, front, back, List(script))
  }

  private def toCards(script: UnicodeKanaScript): List[AnkiCard] =
    Kana
      .buildUnicodeKana(script.codePoint)
      .map(toCard(script.name))

  def generateDeck(scripts: List[UnicodeKanaScript]): List[AnkiCard] =
    (scripts.map(GenerateAnkiCards.toCards) :+ generatePairCards).reduce(_ ++ _)

  private def generatePairCards = {
    val hiragana = Kana.buildUnicodeKana(Kana.hiragana.codePoint)
    val katakana = Kana.buildUnicodeKana(Kana.katakana.codePoint)

    for (n <- hiragana.indices.toList) yield {
      val romaji = Romaji.toRomaji(hiragana(n).variant)

      val cardId = List("hiragana", "katakana", Romaji.toKanaId(hiragana(n).variant)).mkString("-")

      val front = s"""<div id="japanese-kana-pair"><span class="hiragana">${hiragana(n).codePoint.toChar.toString}</span> <span class="katakana">${katakana(n).codePoint.toChar.toString}</span></div>"""

      val back = s"""<div id="japanese-romaji-answer">$romaji</div>"""

      AnkiCard(cardId, front, back, List("pair"))
    }
  }
}

class GenerateAnkiCards[F[_]](implicit F: Sync[F]) {
  def run(args: List[String]): F[ExitCode] =
    for {
      base <- getBaseDir(args)
      _ <- writeDeck(base + "/kana.tsv")(Kana.scripts |> GenerateAnkiCards.generateDeck)
    } yield ExitCode.Success

  private def writeDeck(dest: String) =
    (FilePrinterAlg[F]
      .print(dest) _)
      .compose(Serialization.deckToString)
      .compose(Deck.apply)

  private def getBaseDir(args: List[String]) =
    args match {
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
    }
}