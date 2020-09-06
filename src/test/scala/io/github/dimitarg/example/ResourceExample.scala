package io.github.dimitarg.example

import weaver.pure._
import fs2.Stream
import cats.effect.IO
import cats.effect.Resource
import weaver.Expectations

object ResourceExample extends Suite {

  final case class DatabaseConnection(value: String)
  
  def mkConnection(value: String): Resource[IO, DatabaseConnection] = for {
      _ <- Resource.liftF(IO(println(s"acquiring connection: $value")))
      result <- Resource.pure(DatabaseConnection(value))
  } yield result

  def connTest(conn: DatabaseConnection)(expected: String): IO[Expectations] = for {
      _ <- IO(s"got connection: $conn")
  } yield expect(conn.value == expected)


  val sharedConnectionTests: Stream[IO, RTest[Unit]] = 
    Stream.resource(mkConnection("shared-conn")).flatMap { conn =>
        Stream(
            test("shared connection test")(connTest(conn)("shared-conn")),
            test("shared connection - another test")(connTest(conn)("shared-conn"))
        )
    }

  val ownConnectionTests: Stream[IO, RTest[Unit]] = 
    Stream(
      test("own connection - some test") {
          mkConnection("foo-conn").use { conn =>
           connTest(conn)("foo-conn")
          }
      },
      test("own connection - another test") {
          mkConnection("bar-conn").use { conn =>
           connTest(conn)("bar-conn")
          }
      }
    )

  override def suitesStream: Stream[IO,RTest[Unit]] =
    sharedConnectionTests ++ ownConnectionTests

}