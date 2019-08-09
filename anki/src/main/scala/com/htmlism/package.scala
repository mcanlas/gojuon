package com

package object htmlism {
  case class AnkiCard(id: String, front: String, back: String, tags: List[String])

  case class Deck(cards: List[AnkiCard])
}
