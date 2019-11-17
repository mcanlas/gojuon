package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.any._

object GenerateAnkiCards extends GenerateAnkiCards[IO] with IOApp {
  def toCard(script: String)(uk: UnicodeKana): AnkiCard = {
    val romaji = Romaji.toRomaji(uk.variant)

    val cardId = script + "-" + Romaji.toKanaId(uk.variant)

    val front = s"""<div id="japanese-heroic-character"><span class="$script">${uk.codePoint.toChar.toString}</span></div>"""

    val back = s"""<div id="japanese-romaji-answer">$romaji</div>"""

    AnkiCard(cardId, front, back, List(script))
  }

  def toCards(script: UnicodeKanaScript): List[AnkiCard] =
    Kana
      .buildUnicodeKana(script.codePoint)
      .map(toCard(script.name))
}

class GenerateAnkiCards[F[_]](implicit F: Sync[F]) {
  def run(args: List[String]): F[ExitCode] =
    for {
      baseDir <- getBaseDir(args)
      _ <- writeKanaDeck(baseDir, Kana.scripts)
    } yield ExitCode.Success

  private def writeKanaDeck(base: String, scripts: List[UnicodeKanaScript]) =
    FilePrinterAlg
      .resource[F](base + "/kana.tsv")
      .use(_.print(generateDeck(scripts)))

  private def generateDeck(scripts: List[UnicodeKanaScript]) =
    (scripts.map(generateKanaCards) :+ generatePairCards).reduce(_ ++ _) |>
      Deck.apply |>
      Serialization.deckToString

  private def generatePairCards = {
    val hiragana = Kana.buildUnicodeKana(Kana.hiragana.codePoint)
    val katakana = Kana.buildUnicodeKana(Kana.katakana.codePoint)

    for (n <- hiragana.indices.toList) yield {
      val romaji = Romaji.toRomaji(hiragana(n).variant)

      val cardId = List("hiragana", "katakana", romaji).mkString("-")

      val front = s"""<div id="japanese-kana-pair"><span class="hiragana">${hiragana(n).codePoint.toChar.toString}</span> <span class="katakana">${katakana(n).codePoint.toChar.toString}</span></div>"""

      val back = s"""<div id="japanese-romaji-answer">$romaji</div>"""

      AnkiCard(cardId, front, back, List("pair"))
    }
  }

  private def generateKanaCards(script: UnicodeKanaScript) =
    script |>
      GenerateAnkiCards.toCards

  private def getBaseDir(args: List[String]) =
    args match {
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
    }
}