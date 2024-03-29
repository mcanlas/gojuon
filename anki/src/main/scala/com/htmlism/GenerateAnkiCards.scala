package com.htmlism

import cats.Id
import cats.effect.*
import cats.syntax.all.*

object GenerateAnkiCards extends GenerateAnkiCards[IO] with IOApp

object KanaCards:
  private def kanaToCard(script: String, reg: Map[Int, List[JapaneseEntry]])(uk: UnicodeKana): AnkiCard =
    val romaji = Romaji.toRomaji(uk.variant)

    val cardId = script + "-" + Romaji.toKanaId(uk.variant)

    val front =
      s"""<div id="japanese-heroic-character"><span class="$script">${uk.codePoint.toChar.toString}</span></div>"""

    val back = s"""<div id="japanese-romaji-answer">$romaji</div>"""

    val exampleWords =
      (reg(uk.codePoint)
        .map { je =>
          (je.japanese.s :: je.emoji.toList).mkString(" ")
        }
        .mkString("<br>"): Id[String])
        .map(s => s"""<div>$s</div>""")

    AnkiCard(cardId, front, back + exampleWords, List(script))

  private def scriptToCards(reg: Map[Int, List[JapaneseEntry]])(script: UnicodeKanaScript): List[AnkiCard] =
    Kana
      .buildUnicodeKana(script.codePoint)
      .map(kanaToCard(script.name, reg))

  def generateDeck(scripts: List[UnicodeKanaScript], reg: Map[Int, List[JapaneseEntry]]): List[AnkiCard] =
    scripts.flatMap(scriptToCards(reg)) ::: generatePairCards

  private def generatePairCards =
    val hiragana = Kana.buildUnicodeKana(Kana.hiragana.codePoint)
    val katakana = Kana.buildUnicodeKana(Kana.katakana.codePoint)

    for (n <- hiragana.indices.toList) yield
      val romaji = Romaji.toRomaji(hiragana(n).variant)

      val cardId = List("hiragana", "katakana", Romaji.toKanaId(hiragana(n).variant)).mkString("-")

      val front = s"""<div id="japanese-kana-pair"><span class="hiragana">${hiragana(
          n
        ).codePoint.toChar.toString}</span> <span class="katakana">${katakana(
          n
        ).codePoint.toChar.toString}</span></div>"""

      val back = s"""<div id="japanese-romaji-answer">$romaji</div>"""

      AnkiCard(cardId, front, back, List("pair"))

class GenerateAnkiCards[F[_]](implicit F: Async[F]):
  def run(args: List[String]): F[ExitCode] =
    for
      base <- getBaseDir(args)

      reg <- DataLoader.wordRegistryByCodePoint[F]

      _ <- writeDeck(base + "/kana.tsv")(KanaCards.generateDeck(Kana.scripts, reg))

      words <- DataLoader.allWords[F].map(_.flatten)

      _ <- writeDeck(base + "/phrases.tsv")(PhraseDeck.entriesToAnkiCards(words))
    yield ExitCode.Success

  private def writeDeck(dest: String) =
    FilePrinterAlg[F]
      .print(dest)
      .compose(Serialization.deckToString)
      .compose(Deck.apply)

  private def getBaseDir(args: List[String]) =
    args match
      case head :: _ =>
        F.pure(head)
      case Nil =>
        F.raiseError[String](new IllegalArgumentException("Need to specify where to write file"))
