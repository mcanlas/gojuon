package com

package object htmlism {
  case class AnkiCard(id: String, front: String, back: String)

  case class Deck(cards: List[AnkiCard])
}
