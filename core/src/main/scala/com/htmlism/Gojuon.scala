package com.htmlism

import cats.implicits._

case class UnicodeKanaScript(name: String, codePoint: Int)

object Kana {
  val hiragana: UnicodeKanaScript =
    UnicodeKanaScript("hiragana", 0x3041)

  val katakana: UnicodeKanaScript =
    UnicodeKanaScript("katakana", 0x30A1)

  val scripts: List[UnicodeKanaScript] =
    List(hiragana, katakana)

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

  private def addVariant(f: KanaVaried => KanaVaried)(pred: PartialFunction[Kana, Unit])(kv: KanaVaried) =
    if (pred.isDefinedAt(kv.kana))
      f(kv)
    else
      kv

  val kanaVariants: List[KanaVaried] =
    Kana
      .allKana
      .map(k => KanaVaried(k, hasSmall = false, hasVoiced = false, hasHalf = false))
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

  def buildUnicodeKana(base: Int): List[UnicodeKana] =
    kanaVariants.foldLeft {
      base -> List[UnicodeKana]()
    } {
      buildUnicodeKanaFold
    }._2

  private def buildUnicodeKanaFold(accPair: (Int, List[UnicodeKana]), e: KanaVaried) = {
    val (base, acc) = accPair

    val toCanon =
      howMany(e)(_.hasSmall)

    val uses =
      howMany(e)(_.hasSmall, _.hasHalf, _.hasVoiced)

    val x = UnicodeKana(e, base + toCanon)

    (base + uses + 1) -> (acc :+ x)
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

sealed trait KanaScript
case object Hiragana extends KanaScript
case object Katakana extends KanaScript

case class KanaVaried(kana: Kana, hasSmall: Boolean, hasVoiced: Boolean, hasHalf: Boolean)

case class UnicodeKana(kanaVaried: KanaVaried, codePoint: Int)
