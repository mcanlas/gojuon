package com.htmlism

import cats.effect._
import cats.implicits._
import io.circe._

object DecoderImplicits {
  implicit val japaneseSequenceDecoder: Decoder[JapaneseSequence] =
    Decoder
      .decodeString
      .emap(s => JapaneseSequence.parse(s).leftMap(_.toString))

  implicit val entryDecoder: Decoder[JapaneseEntry] =
    Decoder.forProduct3("j", "k", "e")(JapaneseEntry.apply)
}

object DataLoader {
  import DecoderImplicits._

  private val yamlFiles =
    List(
      "colors",
      "names-american",
      "names-naruto",
      "names-nintendo",
      "nouns",
      "numbers",
      "verbs-irregular",
      "verbs-ru",
      "verbs-u",
      "particles",
      "phrases",
      "places",
      "works")
      .map(s => "/" + s  + ".yaml")

  private def parseResourceFile[F[_]](s: String)(implicit F: Sync[F]) =
    Resource
      .fromAutoCloseable(F.delay(getClass.getResourceAsStream(s)))
      .map(new java.io.InputStreamReader(_))
      .use(reader => F.delay(io.circe.yaml.parser.parse(reader)))
      .flatMap(_.fold(logAndRaise[F, Json](s"parsing json of $s"), F.pure))

  def parseEntries[F[_]](s: String)(implicit F: Sync[F]): F[List[JapaneseEntry]] =
    parseResourceFile[F](s)
      .map(_.as[List[JapaneseEntry]])
      .flatMap(_.fold(logAndRaise[F, List[JapaneseEntry]](s"parsing classes of $s"), F.pure))

  private def logAndRaise[F[_], A](msg: String)(err: Throwable)(implicit F: Sync[F]) =
    F.delay(println(msg + ": " + err)) *> F.raiseError[A](err)

  /**
   * Demonstrate basic loading and parsing of YAML structures
   */
  val demonstrateParsing =
    yamlFiles
      .traverse(parseEntries[IO])
      .map { xxs =>
        xxs.foreach(xs => xs.foreach(println))
      }

  /**
   * Categorize words by kana
   */
  val wordCollectionsByCodePoint: Map[Int, List[JapaneseEntry]] =
    (Kana.unicodeHiraganaByCodePoint ++ Kana.unicodeKatakanaByCodePoint)
      .fmap(_ => List[JapaneseEntry]())

  yamlFiles
    .traverse(parseEntries[IO])
    .map(_.flatten)
    .map { xs =>
      xs
        .foldLeft(wordCollectionsByCodePoint)(organizeByKana)
    }
    .map { reg =>
      for (k <- (Kana.unicodeHiragana ++ Kana.unicodeKatakana)) {
        println(k.codePoint.toChar.toString + " " + k.variant.toString)

        for (e <- reg(k.codePoint)) {
          println("   - " + e)
        }

        for (s <- leftPad(k.codePoint, reg(k.codePoint).map(_.japanese.s))) {
          println("  - " + s)
        }
      }
    }
    .unsafeRunSync()

  def organizeByKana(acc: Map[Int, List[JapaneseEntry]], e: JapaneseEntry) =
    e.japanese.s.toList.toSet.foldLeft(acc) { (acc, k) =>
      if (acc.contains(k.toInt))
        acc.updated(k.toInt, e :: acc(k.toInt) )
      else
        acc
    }

  def leftPad(kana: Int, xs: List[String]) =
    if (xs.isEmpty)
      Nil
    else {
      val maxIndex = xs.map(_.toList.map(_.toInt).indexOf(kana)).max

      xs.map { s =>
        val idx = s.toList.map(_.toInt).indexOf(kana)
        val pad = "\u3000" * (maxIndex - idx)

        pad + s
      }
    }
}
