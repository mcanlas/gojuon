package com.htmlism

import cats.data.NonEmptyList
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

  val kana =
    for {
      c <- consonants
      v <- vowels
    } yield (c, v)

  val cvAvailability: ((Consonant, Vowel)) => Option[Kana] = {
    case (ConsonantY, VowelI | VowelE) =>
      None
    case (ConsonantW, VowelU) =>
      None
    case (c, v) =>
      Kana(c, v).some
  }

  val kanaUnicodeDescriptions =
    kana
      .flatMap(cvAvailability)
      .flatMap {
        case Kana(EmptyConsonant, v) =>
          smallAndLarge(v)
        case Kana(c @ ConsonantT, v @ VowelU) =>
          (c, v, Small) :: (c, v, Unvoiced) :: (c, v, Voiced) :: Nil
        case Kana(c @ ConsonantY, v) =>
          (c, v, Small) :: (c, v, Large) :: Nil
        case Kana(c @ ConsonantW, v @ VowelA) =>
          (c, v, Small) :: (c, v, Large) :: Nil
        case Kana(c @ (ConsonantN | ConsonantM | ConsonantR | ConsonantW), v) =>
          (c, v) :: Nil
        case Kana(c @ ConsonantH, v) =>
          (c, v, Unvoiced) :: (c, v, Voiced) :: (c, v, Half) :: Nil
        case Kana(c @ (ConsonantK | ConsonantS | ConsonantT), v) =>
          (c, v, Unvoiced) :: (c, v, Voiced) :: Nil
      }

  def smallAndLarge(v: Vowel) =
    (v, Small) :: (v, Large) :: Nil

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

case class Kana(consonant: Consonant, vowel: Vowel)

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
case object ConsonantN extends Consonant
case object ConsonantH extends Consonant
case object ConsonantM extends Consonant
case object ConsonantY extends Consonant
case object ConsonantR extends Consonant
case object ConsonantW extends Consonant

sealed trait Variant

/**
 * Also "large" to contrast with "small". And "unvoiced" in voicing.
 */
case object Canonical extends Variant

sealed trait VowelSize extends Variant
case object Small extends VowelSize
case object Large extends VowelSize

sealed trait Voicing extends Variant
case object Voiced extends Voicing
case object Half extends Voicing
case object Unvoiced extends Voicing

sealed trait KanaScript
case object Hiragana extends KanaScript
case object Katakana extends KanaScript

case class KanaVariants(script: KanaScript, variants: NonEmptyList[Variant])
