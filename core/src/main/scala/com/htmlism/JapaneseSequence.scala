package com.htmlism

import cats.syntax.all.*

sealed trait JapaneseSequence:
  def s: String

case class HiraganaSequence(s: String) extends JapaneseSequence

case class KatakanaSequence(s: String) extends JapaneseSequence

sealed trait JapaneseParsingError
case object SequenceMustBeNonEmpty    extends JapaneseParsingError
case object MixedSequenceNotSupported extends JapaneseParsingError
case class CharacterNotKana(c: Char)  extends JapaneseParsingError

object JapaneseSequence:
  val katakanaMidDot           = 12539
  val longVowelSymbolCodePoint = 12540

  def parse(s: String): Either[JapaneseParsingError, JapaneseSequence] =
    for
      nes    <- nonEmpty(s)
      xs     <- nes.toList.traverse(detectScriptOne)
      script <- toScript(xs)
    yield wrap(s)(script)

  private def nonEmpty(s: String) =
    Either.cond(s.nonEmpty, s, SequenceMustBeNonEmpty)

  private def detectScriptOne(c: Char): Either[CharacterNotKana, KanaScript] =
    if isHiragana(c.toInt) then Hiragana.asRight
    else if isKatakana(c.toInt) then Katakana.asRight
    else CharacterNotKana(c).asLeft

  // small ya yu yo
  private def isHiragana(n: Int) =
    Kana.unicodeHiraganaByCodePoint.contains(n) ||
      n == 12387 || // tsu for doubling consontants
      Set(12419, 12421, 12423)(n)

  // small vowels
  private def isKatakana(n: Int) =
    Kana.unicodeKatakanaByCodePoint.contains(n) ||
      n == katakanaMidDot ||
      n == longVowelSymbolCodePoint ||
      n == 12483 ||                             // tsu for doubling consontants
      Set(12515, 12517, 12519)(n) ||            // ya yu yo
      Set(12449, 12451, 12453, 12455, 12457)(n) // small vowels

  private def toScript(xs: List[KanaScript]) =
    if xs.forall(k => k == Hiragana) then Hiragana.asRight
    else if xs.forall(k => k == Katakana) then Katakana.asRight
    else MixedSequenceNotSupported.asLeft

  private def wrap(s: String)(script: KanaScript) =
    script match
      case Katakana => KatakanaSequence(s)
      case Hiragana => HiraganaSequence(s)
