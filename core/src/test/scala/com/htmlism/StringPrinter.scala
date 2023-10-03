package com.htmlism

import cats.effect.*

object StringPrinter extends IOApp.Simple:
  def run: IO[Unit] =
    DataLoader
      .wordRegistryByCodePoint[IO]
      .flatMap(s => IO.delay(println(s)))
