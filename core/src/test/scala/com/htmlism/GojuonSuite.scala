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
}
