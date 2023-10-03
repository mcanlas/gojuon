package com.htmlism

import java.io.PrintWriter

import cats.effect.*

trait FilePrinterAlg[F[_]]:
  def print(dest: String)(s: String): F[Unit]

object FilePrinterAlg:
  def apply[F[_]](implicit F: Async[F]): FilePrinterAlg[F] =
    new FilePrinterAlg[F]:
      def print(dest: String)(s: String): F[Unit] =
        Resource
          .fromAutoCloseable:
            F.delay:
              new PrintWriter(dest)
          .use { pw =>
            F.delay:
              pw.print(s)
          }
