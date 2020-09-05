package io.github.dimitarg.example

import cats.effect.IO
import fs2.Stream
import weaver.pure._
import cats.data.NonEmptyList
import scala.concurrent.duration._

object Examples extends Suite {

  override def suitesStream: Stream[IO,RTest[Unit]] = Stream(
      test("all expectations be true") {
          expectAll(
              NonEmptyList.of(
                expect(1 == 1),
                expect(2 == 2),
                expect(3 == 3),
              )
          )
      },
      test("at least one expectation must be true") {
          expectSome(
              NonEmptyList.of(
                expect(1 == 5),
                expect(2 == 6),
                expect(3 == 3),
              )
          )
      },
      test("timeout") {
        expect(1==1)
          .timeout(10.seconds)
      },
      test("flaky") {
        flaky(attempts = 10000) {
          IO(System.currentTimeMillis()).map { now =>
          expect(now % 2 == 0)  
          }
        }
      },
      ignored("too lazy to fix")(test("this will fail"){
        expect(1 == 3)
      })
  )
  
}
