package com.htmlism

import cats.effect._
import cats.implicits._

object StringPrinter extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    DataLoader
      .wordRegistryByCodePoint[IO]
      .flatMap(s => IO.delay(println(s)))
      .as(ExitCode.Success)
}
