package com.htmlism

import cats._
import cats.effect._
import cats.implicits._

sealed trait JapaneseSequence

case class HiraganaSequence(s: String) extends JapaneseSequence

case class KatakanaSequence(s: String) extends JapaneseSequence

sealed trait JapaneseParsingError
case object SequenceMustBeNonEmpty extends JapaneseParsingError
case object MixedSequenceNotSupported extends JapaneseParsingError
case class CharacterNotKana(c: Char) extends JapaneseParsingError

object JapaneseSequence {
  def parse(s: String): Either[JapaneseParsingError, JapaneseSequence] =
    for {
      nes <- nonEmpty(s)
      xs <- nes.toList.traverse(detectScriptOne)
      script <- toScript(xs)
    } yield wrap(s)(script)

  private def nonEmpty(s: String) =
    Either.cond(s.nonEmpty, s, SequenceMustBeNonEmpty)

  private def detectScriptOne(c: Char): Either[CharacterNotKana, KanaScript] =
    if (Kana.unicodeHiraganaByCodePoint.contains(c.toInt))
      Hiragana.asRight
    else if (Kana.unicodeKatakanaByCodePoint.contains(c.toInt))
      Katakana.asRight
    else
      CharacterNotKana(c).asLeft

  private def toScript(xs: List[KanaScript]) =
    if (xs.forall(k => k == Hiragana))
      Hiragana.asRight
    else if (xs.forall(k => k == Katakana))
      Katakana.asRight
    else
      MixedSequenceNotSupported.asLeft

  private def wrap(s: String)(script: KanaScript) =
    script match {
      case Katakana => KatakanaSequence(s)
      case Hiragana => HiraganaSequence(s)
    }
}
