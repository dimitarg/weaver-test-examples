package io.github.dimitarg.example

import weaver.pure._
import cats.effect.IO
import fs2.Stream
object MinimalTest extends Suite {
  override def suitesStream: fs2.Stream[IO,RTest[Unit]] = Stream(
    test("reality is still in place") {
      expect(1 == 1)
    }
  )
}
