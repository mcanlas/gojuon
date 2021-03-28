package com.htmlism

import cats.effect._

object StringPrinter extends IOApp.Simple {
  def run: IO[Unit] =
    DataLoader
      .wordRegistryByCodePoint[IO]
      .flatMap(s => IO.delay(println(s)))
}
