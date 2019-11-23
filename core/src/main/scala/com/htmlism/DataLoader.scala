package com.htmlism

import cats.effect._
import cats.implicits._
import io.circe._

object DataLoader extends App {
  private implicit val entryDecoder: Decoder[JapaneseEntry] =
    Decoder.forProduct3("j", "k", "e")(JapaneseEntry.apply)

  private val yamlFiles =
    List(
      "names-naruto",
      "names-nintendo",
      "nouns",
      "verbs-irregular",
      "verbs-ru",
      "verbs-u",
      "particles",
      "phrases")
      .map(s => "/" + s  + ".yaml")

  private def parseResourceFile[F[_]](s: String)(implicit F: Sync[F]) =
    Resource
      .fromAutoCloseable(F.delay(getClass.getResourceAsStream(s)))
      .map(new java.io.InputStreamReader(_))
      .use(reader => F.delay(io.circe.yaml.parser.parse(reader)))
      .flatMap(_.fold(logAndRaise[F, Json](s"parsing json of $s"), F.pure))

  private def parseEntries[F[_]](s: String)(implicit F: Sync[F]) =
    parseResourceFile[F](s)
      .map(_.as[List[JapaneseEntry]])
      .flatMap(_.fold(logAndRaise[F, List[JapaneseEntry]](s"parsing classes of $s"), F.pure))

  private def logAndRaise[F[_], A](msg: String)(err: Throwable)(implicit F: Sync[F]) =
    F.delay(println(msg + ": " + err)) *> F.raiseError[A](err)

  /**
   * Demonstrate basic loading and parsing of YAML structures
   */
  yamlFiles
    .traverse(parseEntries[IO])
    .map { xxs =>
      xxs.foreach(xs => xs.foreach(println))
    }
    .unsafeRunSync()

  /**
   * Consume YAML structures and parse out Japanese sequences
   */
  yamlFiles
    .traverse(parseEntries[IO])
    .map(_.flatten)
    .map { xs =>
      xs.foreach { e =>
        println(JapaneseSequence.parse(e.japanese))
      }
    }
    .unsafeRunSync()

  /**
   * Categorize words by kana
   */
  val wordCollectionsByCodePoint: Map[Int, List[JapaneseSequence]] =
    (Kana.unicodeHiraganaByCodePoint ++ Kana.unicodeKatakanaByCodePoint)
      .fmap(_ => List[JapaneseSequence]())

  yamlFiles
    .traverse(parseEntries[IO])
    .map(_.flatten)
    .map { xs =>
      xs
        .flatMap(e => JapaneseSequence.parse(e.japanese).toOption)
        .foldLeft(wordCollectionsByCodePoint)(organizeByKana)
    }
    .map { reg =>
      for (h <- (Kana.unicodeHiragana ++ Kana.unicodeKatakana)) {
        println(h.codePoint.toChar)
        println("  " + reg(h.codePoint).toString)
      }
    }
    .unsafeRunSync()

  def organizeByKana(acc: Map[Int, List[JapaneseSequence]], e: JapaneseSequence) =
    e.s.toList.toSet.foldLeft(acc) { (acc, k) =>
      if (k.toInt == JapaneseSequence.longVowelSymbolCodePoint)
        acc
      else
        acc.updated(k.toInt, e :: acc(k.toInt) )
    }
}
