package com.htmlism

object Romaji {
  val vowels: Map[Vowel, String] =
    Map(
      VowelA -> "a",
      VowelE -> "e",
      VowelI -> "i",
      VowelO -> "o",
      VowelU -> "u"
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
      ConsonantW     -> "w"
    )

  val voicedConsonants: Map[Consonant, String] =
    Map(ConsonantK -> "g", ConsonantS -> "z", ConsonantT -> "d", ConsonantH -> "b")

  val halfVoicedH: String = "p"

  def toRomaji(variant: KanaVariant): String =
    variant match {
      case VoicedKanaVariant(KanaCv(ConsonantS, VowelI)) =>
        "ji"

      case VoicedKanaVariant(KanaCv(ConsonantT, VowelI)) =>
        "ji"

      case VoicedKanaVariant(KanaCv(ConsonantT, VowelU)) =>
        "zu"

      case _ =>
        toKanaId(variant)
    }

  def toKanaId(variant: KanaVariant): String =
    variant match {
      case HalfVoicedKanaVariant(KanaCv(ConsonantH, v)) =>
        halfVoicedH + vowels(v)

      case VoicedKanaVariant(KanaCv(c, v)) =>
        voicedConsonants(c) + vowels(v)

      case UnvoicedKanaVariant(kana) =>
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
      case _ =>
        throw new UnsupportedOperationException("kana variation does not exist")
    }
}
