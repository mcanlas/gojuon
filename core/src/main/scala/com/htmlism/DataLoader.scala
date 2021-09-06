package com.htmlism

import cats.effect._
import cats.implicits._
import io.circe._

object DecoderImplicits {
  implicit val japaneseSequenceDecoder: Decoder[JapaneseSequence] =
    Decoder.decodeString
      .emap(s => JapaneseSequence.parse(s).leftMap(_.toString))

  private val defaultTagDecoder =
    Nil.asRight: Decoder.Result[List[String]]

  implicit val entryDecoder: Decoder[JapaneseEntry] =
    new Decoder[JapaneseEntry] {
      final def apply(c: HCursor): Decoder.Result[JapaneseEntry] =
        for {
          id <- c.downField("id").as[Option[String]]
          j <- c.downField("j").as[JapaneseSequence]
          k <- c.downField("k").as[Option[String]]
          e <- c.downField("e").as[String]
          emoji <- c.downField("emoji").as[Option[String]]
          tags <- c.downField("tag").focus.fold(defaultTagDecoder)(decodeTagMulti)
        } yield {
          new JapaneseEntry(id, j, k, e, emoji, tags)
        }
    }

  private def decodeTagMulti(j: Json) =
    if (j.isString)
      j.as[String].map(List(_))
    else if (j.isArray)
      j.as[List[String]]
    else
      DecodingFailure("expected string or array", Nil).asLeft

}

object DataLoader {
  import DecoderImplicits._

  private val yamlFiles =
    List(
      "colors",
      "companies",
      "names-american",
      "names-fiction",
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
      "works"
    )

  val enhancements = Map[String, JapaneseEntry => JapaneseEntry](
    "phrases" -> (_.withTag("phrase"))
  )

  private def parseResourceFile[F[_]](s: String)(implicit F: Async[F]) =
    Resource
      .fromAutoCloseable(F.delay(getClass.getResourceAsStream("/" + s + ".yaml")))
      .map(new java.io.InputStreamReader(_))
      .use(reader => F.delay(io.circe.yaml.parser.parse(reader)))
      .flatMap(_.fold(logAndRaise[F, Json](s"parsing json of $s"), F.pure))

  def parseEntries[F[_]](s: String)(implicit F: Async[F]): F[List[JapaneseEntry]] =
    parseResourceFile[F](s)
      .map(_.as[List[JapaneseEntry]])
      .flatMap(_.fold(logAndRaise[F, List[JapaneseEntry]](s"parsing classes of $s"), F.pure))
      .map { xs => enhancements.get(s).fold(xs)(f => xs.map(f)) }

  private def logAndRaise[F[_], A](msg: String)(err: Throwable)(implicit F: Sync[F]) =
    F.delay(println(msg + ": " + err)) *> F.raiseError[A](err)

  def allWords[F[_]: Async] =
    yamlFiles
      .traverse(parseEntries[F])

  /**
    * Demonstrate basic loading and parsing of YAML structures
    */
  def demonstrateParsing[F[_]: Async] =
    allWords[F]
      .map { xxs =>
        xxs.foreach(xs => xs.foreach(println))
      }

  /**
    * Categorize words by kana
    */
  val wordCollectionsByCodePoint: Map[Int, List[JapaneseEntry]] =
    (Kana.unicodeHiraganaByCodePoint ++ Kana.unicodeKatakanaByCodePoint)
      .fmap(_ => List[JapaneseEntry]())

  def wordRegistryByCodePoint[F[_]: Async] =
    allWords[F]
      .map(_.flatten)
      .map(_.foldLeft(wordCollectionsByCodePoint)(organizeByKana))

  import cats.effect.unsafe.implicits.global

  wordRegistryByCodePoint[IO]
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
        acc.updated(k.toInt, e :: acc(k.toInt))
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
