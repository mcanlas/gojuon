package com.htmlism

case class JapaneseEntry(japanese: JapaneseSequence, kanji: Option[String], english: String, emoji: Option[String], tags: List[String]) {
  def withTag(s: String): JapaneseEntry =
    copy(tags = s :: tags)
}
