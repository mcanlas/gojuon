package com.htmlism

import cats.effect._
import cats.implicits._

object Gojuon extends IOApp {
  private def addVariant(f: KanaVariants => KanaVariants)(pred: PartialFunction[Kana, Unit])(kv: KanaVariants) =
    if (pred.isDefinedAt(kv.kana))
      f(kv)
    else
      kv

  private[this] val kanaUnicodeDescriptions =
    Kana
      .allKana
      .map(k => KanaVariants(k, hasSmall = false, hasVoiced = false, hasHalf = false))
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(EmptyConsonant | ConsonantY, _) => })
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(EmptyConsonant | ConsonantY, _) => })
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(ConsonantT, VowelU) => })
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(ConsonantW, VowelA) => })
      .map(addVariant(_.copy(hasVoiced = true)) { case KanaCv(ConsonantH | ConsonantK | ConsonantS | ConsonantT, _) => })
      .map(addVariant(_.copy(hasHalf   = true)) { case KanaCv(ConsonantH, _) => })
      .flatMap { kv =>
        val small =
          if (kv.hasSmall)
            List(kv.kana -> Small)
          else
            Nil

        val voiced =
          if (kv.hasVoiced)
            List(kv.kana -> Voiced)
          else
            Nil

        val half =
          if (kv.hasHalf)
            List(kv.kana -> Half)
          else
            Nil

        val canonical =
          List(kv.kana -> Canonical)

        small ::: canonical ::: voiced ::: half
      }

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