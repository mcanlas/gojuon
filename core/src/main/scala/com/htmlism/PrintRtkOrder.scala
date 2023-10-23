package com.htmlism

import cats.effect.*

object PrintRtkOrder extends IOApp.Simple:
  private val justCanonicalFormsHiragana =
    Kana
      .unicodeHiragana
      .filter:
        case UnicodeKana(UnvoicedKanaVariant(KanaCv(ConsonantW, VowelE | VowelI)), _) =>
          false
        case UnicodeKana(UnvoicedKanaVariant(_), _) =>
          true
        case _ =>
          false

  private val justCanonicalFormsKatakana =
    Kana
      .unicodeKatakana
      .filter:
        case UnicodeKana(UnvoicedKanaVariant(KanaCv(ConsonantW, VowelE | VowelI)), _) =>
          false
        case UnicodeKana(UnvoicedKanaVariant(_), _) =>
          true
        case _ =>
          false

  def run: IO[Unit] =
    for
      xs <- readFile("htk-hiragana.tsv")

      ys <- readFile("htk-katakana.tsv")

      _ <- IO.delay:
        xs
          .zip(justCanonicalFormsHiragana)
          .sortBy(_._1)
          .foreach { case (sort, u) =>
            println(s"${u.codePoint.toChar} $sort")
          }

      _ <- IO.delay { println(); println(); println() }

      _ <- IO.delay:
        ys
          .zip(justCanonicalFormsKatakana)
          .sortBy(_._1)
          .foreach { case (sort, u) =>
            println(s"${u.codePoint.toChar} $sort")
          }
    yield ()

  def readFile(s: String) =
    Resource
      .fromAutoCloseable(IO.delay(getClass.getResourceAsStream("/" + s)))
      .use(s =>
        IO.delay {
          scala.io.Source.fromInputStream(s).getLines().toList
        }
      )
      .map(_.tail)
      .map(_.flatMap { s =>
        val sixCols = s.split("\t")

        sixCols.toList.tail.filter(_.nonEmpty)
      })
