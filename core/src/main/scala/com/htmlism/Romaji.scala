package com.htmlism

object Romaji {
  val vowels: Map[Vowel, String] =
    Map(
      VowelA -> "a",
      VowelE -> "e",
      VowelI -> "i",
      VowelO -> "o",
      VowelU -> "u",
    )

  val consonants: Map[Consonant, String] =
    Map(
      EmptyConsonant -> "",
      ConsonantK     -> "k",
      ConsonantS     -> "s",
      ConsonantT     -> "t",
      ConsonantN     -> "n",
      ConsonantH     -> "h",
      ConsonantM     -> "m",
      ConsonantY     -> "y",
      ConsonantR     -> "r",
      ConsonantW     -> "w")

  val voicedConsonants: Map[Consonant, String] =
    Map(
      ConsonantK -> "g",
      ConsonantS -> "z",
      ConsonantT -> "d",
      ConsonantH -> "b")

  val halfVoicedH: String = "p"

  def toRomaji(kana: Kana): String =
    kana match {
      case KanaCv(ConsonantS, VowelI) =>
        "shi"
      case KanaCv(ConsonantT, VowelI) =>
        "chi"
      case KanaCv(ConsonantT, VowelU) =>
        "tsu"
      case KanaCv(ConsonantH, VowelU) =>
        "fu"
      case KanaCv(c, v) =>
        consonants(c) + vowels(v)
      case ConsonantN =>
        consonants(ConsonantN)
    }
}
