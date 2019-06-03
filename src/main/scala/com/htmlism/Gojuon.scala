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
    List(ConsonantK,
      ConsonantS,
      ConsonantT,
      ConsonantN,
      ConsonantH,
      ConsonantM,
      ConsonantY,
      ConsonantR,
      ConsonantW)

  val kanaDescription =
    vowels
      .flatMap(v => (v, Small) :: (v, Large) :: Nil) ++
    consonants
      .flatMap { c =>
        vowels
          .flatMap { v =>
            (c, v) match {
              case (ConsonantT, VowelU) =>
                (c, v, Small) :: (c, v, Unvoiced) :: (c, v, Voiced) :: Nil
              case (ConsonantN, _) =>
                (c, v) :: Nil
              case (ConsonantH, _) =>
                (c, v, Unvoiced) :: (c, v, Voiced) :: (c, v, Half) :: Nil
              case _ =>
                (c, v, Unvoiced) :: (c, v, Voiced) :: Nil
            }
          }
      }

  val kana =
    (for {
      v <- vowels
      c <- consonants
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

  def run(args: List[String]): IO[ExitCode] =
    IO {
      for (n <- 0 to 100) {
        println {
          kanaDescription(n) + ": " + (hiraganaCodepoint + n).toChar
        }
      }

      println
      println
      println

      for (n <- 0 to 100) {
        println {
          kanaDescription(n) + ": " + (katakanaCodepoint + n).toChar
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
