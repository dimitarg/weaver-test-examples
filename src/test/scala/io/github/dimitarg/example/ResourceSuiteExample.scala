package io.github.dimitarg.example

import weaver.pure._
import cats.implicits._
import fs2.Stream
import cats.effect.Resource
import cats.effect.IO

object ResourceSuiteExample extends RSuite {

  final case class DatabaseConnection(value: String)

  override type R = DatabaseConnection

  override def sharedResource: Resource[IO, DatabaseConnection] = for {
      _ <- Resource.liftF(IO(println(s"acquiring shared connection")))
      result <- Resource.pure[IO, DatabaseConnection](DatabaseConnection("shared-conn"))
  } yield result


  override def suitesStream: fs2.Stream[IO,RTest[DatabaseConnection]] = Stream(
    rTest("some test") { conn =>
      IO(println(s"got connection $conn")) >>
        expect(1 == 1)
    },
    rTest("some other test") { conn =>
      IO(println(s"got connection $conn")) >>
        expect(2 == 2)
    }
  )
}
