package io.github.dimitarg

import cats.implicits._
import cats.data.NonEmptyList
import cats.effect.IO
import weaver.Expectations
import weaver.Expectations.Additive
import weaver.pure.RTest
import weaver.SourceLocation
import weaver.IgnoredException

package object example {

  def expectAll(xs: NonEmptyList[IO[Expectations]]): IO[Expectations] =  {
    xs.sequence.map(_.fold)
  }

  def expectSome(xs: NonEmptyList[IO[Expectations]]): IO[Expectations] =  {
    xs.sequence.map { xs =>
      xs.map(Additive(_)).fold
    }.map(Additive.unwrap)
  }

  def flaky(attempts: Int)(x: IO[Expectations]): IO[Expectations] = {
      if(attempts<1) {
          x
      } else {
          x.attempt.flatMap(
            _.fold[IO[Expectations]](
              _ => flaky(attempts-1)(x),
                result => {
                  if(result.run.isValid) {
                    result.pure[IO]
                  } else {
                    flaky(attempts-1)(x)  
                  }  
                }  
              )
          )
      }
  }

  def ignored[A](reason: String)(x: RTest[A])(implicit loc: SourceLocation): RTest[A] = RTest(
      x.name, _ => IO.raiseError(new IgnoredException(reason.some, loc))
  )

}
