package com.htmlism

import cats.data.NonEmptyList
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
      .flatMap(availableKana)
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

case class KanaVariants(kana: Kana, variants: NonEmptyList[Variant]) {
  def prepend(v: Variant): KanaVariants =
    this.copy(variants = variants.prepend(v))

  def append(v: Variant): KanaVariants =
    this.copy(variants = variants.append(v))
}

object KanaVariants {
  def canonical(kana: Kana): KanaVariants =
    KanaVariants(kana, NonEmptyList.of(Canonical))
}

