package com.htmlism

import cats.effect._
import cats.implicits._
import mouse.any._

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
    (for {
      c <- consonants
      v <- vowels
    } yield (c, v))
      .map {
        case (ConsonantY, VowelI) =>
          Unavailable
        case (ConsonantY, VowelE) =>
          Unavailable
        case (ConsonantW, VowelU) =>
          Unavailable
        case (c, v) =>
          Kana(c, v)
      }

  val kanaUnicodeDescriptions =
    kana
      .collect { case k: Kana => k }
      .flatMap { k =>
        k match {
          case Kana(EmptyConsonant, v) =>
            smallAndLarge(v)
          case Kana(c @ ConsonantT, v @ VowelU) =>
            (c, v, Small) :: (c, v, Unvoiced) :: (c, v, Voiced) :: Nil
          case Kana(c @ ConsonantN, v) =>
            (c, v) :: Nil
          case Kana(c @ ConsonantH, v) =>
            (c, v, Unvoiced) :: (c, v, Voiced) :: (c, v, Half) :: Nil
          case Kana(c, v) =>
            (c, v, Unvoiced) :: (c, v, Voiced) :: Nil
        }
      }

  def smallAndLarge(v: Vowel) =
    (v, Small) :: (v, Large) :: Nil

  def run(args: List[String]): IO[ExitCode] =
    IO {
      for (n <- 0 to 100) {
        println {
          kanaUnicodeDescriptions(n) + ": " + (hiraganaCodepoint + n).toChar
        }
      }

      println
      println
      println

      for (n <- 0 to 100) {
        println {
          kanaUnicodeDescriptions(n) + ": " + (katakanaCodepoint + n).toChar
        }
      }
    }.as(ExitCode.Success)
}

sealed trait KanaAvailability
case class Kana(consonant: Consonant, vowel: Vowel) extends KanaAvailability
case object Unavailable extends KanaAvailability

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

sealed trait VowelSize
case object Small extends VowelSize
case object Large extends VowelSize

sealed trait Voicing
case object Voiced extends Voicing
case object Half extends Voicing
case object Unvoiced extends Voicing
