package com.htmlism

object PhraseDeck:
  def entriesToAnkiCards(xs: List[JapaneseEntry]): List[AnkiCard] =
    xs
      .filter(_.tags.contains("phrase"))
      .map(entryToAnkiCard)

  private def entryToAnkiCard(je: JapaneseEntry) =
    AnkiCard(
      je.japanese.s.hashCode.toString,
      "<div>" + je.japanese.s + "\u3002" + "</div>",
      "<div>" + je.english + "</div>",
      Nil
    )
