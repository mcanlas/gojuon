package com.htmlism

import java.io.PrintWriter

import cats.effect._

trait FilePrinterAlg[F[_]] {
  def print(s: String): F[Unit]
}

object FilePrinterAlg {
  def apply[F[_]](pw: PrintWriter)(implicit F: Sync[F]): FilePrinterAlg[F] =
    new FilePrinterAlg[F] {
      def print(s: String): F[Unit] =
        F.delay {
          pw.print(s)
        }
    }

  def resource[F[_]](dest: String)(implicit F: Sync[F]): Resource[F, FilePrinterAlg[F]] =
    Resource
      .fromAutoCloseable {
        F.delay {
          new PrintWriter(dest)
        }
      }
      .map(FilePrinterAlg[F])
}