package com.htmlism

import cats.effect._
import cats.implicits._

object Gojuon extends IOApp {
  val hiraganaCodepoint = 0x3041
  val katakanaCodepoint = 0x30A1

  val vowels =
    List(VowelA, VowelI, VowelU, VowelE, VowelO)

  val consonants =
    List(
      EmptyConsonant,
      ConsonantK,
      ConsonantS,
      ConsonantT,
      ConsonantN,
      ConsonantH,
      ConsonantM,
      ConsonantY,
      ConsonantR,
      ConsonantW)

  val cvCombinations =
    for {
      c <- consonants
      v <- vowels
    } yield (c, v)

  val availableKana: ((Consonant, Vowel)) => Option[Kana] = {
    case (ConsonantY, VowelI | VowelE) =>
      None
    case (ConsonantW, VowelU) =>
      None
    case (c, v) =>
      KanaCv(c, v).some
  }

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
    cvCombinations
      .flatMap(availableKana)
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
          kanaUnicodeDescriptions(n) + ": " + (hiraganaCodepoint + n).toChar
        }
      }

      println
      println
      println

      for (n <- kanaUnicodeDescriptions.indices) {
        println {
          kanaUnicodeDescriptions(n) + ": " + (katakanaCodepoint + n).toChar
        }
      }

    }.as(ExitCode.Success)
}