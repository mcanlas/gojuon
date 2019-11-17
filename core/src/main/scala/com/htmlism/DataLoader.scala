package com.htmlism

import cats.effect._
import cats.implicits._
import io.circe._

object DataLoader extends App {
  private implicit val entryDecoder: Decoder[JapaneseEntry] =
    Decoder.forProduct3("j", "k", "e")(JapaneseEntry.apply)

  private val yamlFiles =
    List("particles", "phrases")
      .map(s => "/" + s  + ".yaml")

  private def parseResourceFile[F[_]](s: String)(implicit F: Sync[F]) =
    Resource
      .fromAutoCloseable(F.delay(getClass.getResourceAsStream(s)))
      .map(new java.io.InputStreamReader(_))
      .use(reader => F.delay(io.circe.yaml.parser.parse(reader)))
      .flatMap(_.fold(F.raiseError[Json], F.pure))

  private def parseEntries[F[_]](s: String)(implicit F: Sync[F]) =
    parseResourceFile[F](s)
      .map(_.as[List[JapaneseEntry]])
      .flatMap(_.fold(F.raiseError[List[JapaneseEntry]], F.pure))

  yamlFiles
    .traverse(parseEntries[IO])
    .map { xxs =>
      xxs.foreach(xs => xs.foreach(println))
    }
    .unsafeRunSync()
}