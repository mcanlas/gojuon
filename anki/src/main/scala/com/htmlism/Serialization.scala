package com.htmlism

object Serialization {
  def cardtoString(card: AnkiCard): String =
    List(card.id, card.front, card.back, card.tags.mkString(" "))
      .mkString("\t")

  def deckToString(deck: Deck): String =
    deck.cards
      .map(cardtoString)
      .map(_ + "\n")
      .mkString
}
