package com.htmlism

import org.scalatest._

class GojuonSuite extends FunSuite with Matchers {
  test("the number of canonical kana should be 48") {
    Kana.kanaVariants.length shouldBe 48
  }

  test("hiragana ka should have two voicings") {
    val ka = Kana.kanaVariants(5)

    val voicings =
      List(
        UnvoicedKanaVariant(KanaCv(ConsonantK, VowelA)),
        VoicedKanaVariant(KanaCv(ConsonantK, VowelA))
      )

    KanaVariant
      .listVoicings(ka) should contain theSameElementsInOrderAs voicings
  }

  test("hiragana ha should have three voicings") {
    val ha = Kana.kanaVariants(25)

    val voicings =
      List(
        UnvoicedKanaVariant(KanaCv(ConsonantH, VowelA)),
        VoicedKanaVariant(KanaCv(ConsonantH, VowelA)),
        HalfVoicedKanaVariant(KanaCv(ConsonantH, VowelA))
      )

    KanaVariant
      .listVoicings(ha) should contain theSameElementsInOrderAs voicings
  }
}
