package com.htmlism

import org.scalatest._

class GojuonSuite extends FunSuite with Matchers {
  test("the number of canonical kana for hiragana should be 48") {
    Kana
      .buildUnicodeKana(Kana.hiragana.codePoint)
      .length shouldBe 48
  }

  test("the number of canonical kana for katakana should be 48") {
    Kana
      .buildUnicodeKana(Kana.hiragana.codePoint)
      .length shouldBe 48
  }

  test("hiragana ka should have two voicings") {
    val kana =
      Kana.buildUnicodeKana(Kana.hiragana.codePoint)

    val ka = kana(5)

    val voicings =
      List(
        UnvoicedKanaVariant(KanaCv(ConsonantK, VowelA), 12363),
        VoicedKanaVariant(KanaCv(ConsonantK, VowelA), 12364)
      )

    UnicodeKanaVariant
      .listVoicings(ka) should contain theSameElementsInOrderAs voicings
  }

  test("hiragana ha should have three voicings") {
    val kana =
      Kana.buildUnicodeKana(Kana.hiragana.codePoint)

    val ha = kana(25)

    val voicings =
      List(
        UnvoicedKanaVariant(KanaCv(ConsonantH, VowelA), 12399),
        VoicedKanaVariant(KanaCv(ConsonantH, VowelA), 12400),
        HalfVoicedKanaVariant(KanaCv(ConsonantH, VowelA), 12401)
      )

    UnicodeKanaVariant
      .listVoicings(ha) should contain theSameElementsInOrderAs voicings
  }
}
