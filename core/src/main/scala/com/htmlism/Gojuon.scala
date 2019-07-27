package com.htmlism

import cats.implicits._

object Kana {
  val hiraganaCodepoint = 0x3041
  val katakanaCodepoint = 0x30A1

  val vowels: List[Vowel] =
    List(VowelA, VowelI, VowelU, VowelE, VowelO)

  val consonants: List[Consonant] =
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

  private val cvCombinations =
    for {
      c <- consonants
      v <- vowels
    } yield (c, v)

  private val availableKana: ((Consonant, Vowel)) => Option[Kana] = {
    case (ConsonantY, VowelI | VowelE) =>
      None
    case (ConsonantW, VowelU) =>
      None
    case (c, v) =>
      KanaCv(c, v).some
  }

  val allKana: List[Kana] =
    cvCombinations
      .flatMap(availableKana) :+ ConsonantN

  private def addVariant(f: KanaVariants => KanaVariants)(pred: PartialFunction[Kana, Unit])(kv: KanaVariants) =
    if (pred.isDefinedAt(kv.kana))
      f(kv)
    else
      kv

  val kanaVariants: List[KanaVariants] =
    Kana
      .allKana
      .map(k => KanaVariants(k, hasSmall = false, hasVoiced = false, hasHalf = false))
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(EmptyConsonant | ConsonantY, _) => })
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(EmptyConsonant | ConsonantY, _) => })
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(ConsonantT, VowelU) => })
      .map(addVariant(_.copy(hasSmall  = true)) { case KanaCv(ConsonantW, VowelA) => })
      .map(addVariant(_.copy(hasVoiced = true)) { case KanaCv(ConsonantH | ConsonantK | ConsonantS | ConsonantT, _) => })
      .map(addVariant(_.copy(hasHalf   = true)) { case KanaCv(ConsonantH, _) => })

  private def howMany[A](x: A)(fs: (A => Boolean)*) =
    fs
      .map(_.apply(x))
      .map(if (_) 1 else 0)
      .sum

  @scala.annotation.tailrec
  def buildUnicodeKana(
    base: Int,
    variants: List[KanaVariants],
    acc: List[UnicodeKana]): List[UnicodeKana] =
    variants match {
      case head :: tail =>
        val toCanon =
          howMany(head)(_.hasSmall)

        val uses =
          howMany(head)(_.hasSmall, _.hasHalf, _.hasVoiced)

        val x = UnicodeKana(head.kana, base + toCanon)

        buildUnicodeKana(base + uses + 1, tail, acc :+ x)
      case Nil =>
        acc
    }
}

sealed trait Kana

case class KanaCv(consonant: Consonant, vowel: Vowel) extends Kana

sealed trait Vowel
case object VowelA extends Vowel
case object VowelI extends Vowel
case object VowelU extends Vowel
case object VowelE extends Vowel
case object VowelO extends Vowel

sealed trait Consonant
case object EmptyConsonant extends Consonant
case object ConsonantK extends Consonant
case object ConsonantS extends Consonant
case object ConsonantT extends Consonant
case object ConsonantN extends Consonant with Kana
case object ConsonantH extends Consonant
case object ConsonantM extends Consonant
case object ConsonantY extends Consonant
case object ConsonantR extends Consonant
case object ConsonantW extends Consonant

sealed trait Variant

/**
 * Also "Canonical" to contrast with "small". And "unvoiced" in voicing.
 */
case object Canonical extends Variant

case object Small extends Variant

sealed trait Voicing extends Variant
case object Voiced extends Voicing
case object Half extends Voicing

sealed trait KanaScript
case object Hiragana extends KanaScript
case object Katakana extends KanaScript

case class KanaVariants(kana: Kana, hasSmall: Boolean, hasVoiced: Boolean, hasHalf: Boolean)

case class UnicodeKana(kana: Kana, codePoint: Int)
