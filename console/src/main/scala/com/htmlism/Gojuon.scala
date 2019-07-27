package com.htmlism

import cats.effect._
import cats.implicits._

object Gojuon extends IOApp {
  private def prepend(v: Variant)(pred: PartialFunction[Kana, Unit])(kv: KanaVariants) =
    if (pred.isDefinedAt(kv.kana))
      kv.prepend(v)
    else
      kv

  private def append(v: Variant)(pred: PartialFunction[Kana, Unit])(kv: KanaVariants) =
    if (pred.isDefinedAt(kv.kana))
      kv.append(v)
    else
      kv

  private[this] val kanaUnicodeDescriptions =
    Kana
      .allKana
      .map(KanaVariants.canonical)
      .map(prepend(Small) { case KanaCv(EmptyConsonant | ConsonantY, _) => })
      .map(prepend(Small) { case KanaCv(ConsonantT, VowelU) => })
      .map(prepend(Small) { case KanaCv(ConsonantW, VowelA) => })
      .map(append(Voiced) { case KanaCv(ConsonantH | ConsonantK | ConsonantS | ConsonantT, _) => })
      .map(append(Half) { case KanaCv(ConsonantH, _) => })
      .flatMap(kv => kv.variants.map(v => kv.kana -> v).toList) :+ ConsonantN

  def run(args: List[String]): IO[ExitCode] =
    IO {
      println(s"number of kana variants is ${kanaUnicodeDescriptions.size}")

      for (n <- kanaUnicodeDescriptions.indices) {
        println {
          kanaUnicodeDescriptions(n) + ": " + (Kana.hiraganaCodepoint + n).toChar
        }
      }

      println
      println
      println

      for (n <- kanaUnicodeDescriptions.indices) {
        println {
          kanaUnicodeDescriptions(n) + ": " + (Kana.katakanaCodepoint + n).toChar
        }
      }

    }.as(ExitCode.Success)
}