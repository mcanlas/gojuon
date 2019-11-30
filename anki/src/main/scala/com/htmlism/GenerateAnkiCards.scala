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
    (scripts.map(generateKanaCards) :+ generatePairCards).reduce(_ ++ _)

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

  private def generateKanaCards(script: UnicodeKanaScript) =
    script |>
      GenerateAnkiCards.toCards
}

class GenerateAnkiCards[F[_]](implicit F: Sync[F]) {
  def run(args: List[String]): F[ExitCode] =
    for {
      base <- getBaseDir(args)
      _ <- writeDeck(Kana.scripts |> GenerateAnkiCards.generateDeck)(base + "/kana.tsv")
    } yield ExitCode.Success

  private def writeDeck(cards: List[AnkiCard])(dest: String) =
    (FilePrinterAlg[F]
      .print(dest) _)
      .compose(Serialization.deckToString)
      .compose(Deck.apply)
      .apply(cards)

  private def getBaseDir(args: List[String]) =
    args match {
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
    }
}