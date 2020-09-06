package io.github.dimitarg.example

import weaver.pure._
import fs2._
import cats.effect.IO
import scala.math.BigDecimal.RoundingMode
import scala.concurrent.duration._

object RoundingSpec extends Suite {

  final case class EnergyBalance(value: BigDecimal)
  final case class Pence(value: Int)

  def roundInFavourOfCustomer(balance: EnergyBalance): Pence = {
      val roundingMode = if (balance.value >= 0) {
          RoundingMode.UP
      } else {
          RoundingMode.DOWN
      }

      val rounded = balance.value.setScale(2, roundingMode)
      val pence = (rounded * 100).toIntExact
      Pence(pence)
  }

  final case class TestScenario(scenarioName: String, energyBalance: EnergyBalance, expectedResult: Pence)

  val testData: Stream[Pure, TestScenario] = Stream(
    TestScenario("positive - nothing to round", EnergyBalance(2.49)   ,  Pence(249)),
    TestScenario("positive rounds up",          EnergyBalance(2.494)  ,  Pence(250)),
    TestScenario("positive rounds up - 2",      EnergyBalance(2.491)  ,  Pence(250)),
    TestScenario("negative - nothing to round", EnergyBalance(-2.49)  ,  Pence(-249)),
    TestScenario("negative rounds down",        EnergyBalance(-2.491) ,  Pence(-249)),
    TestScenario("negative rounds down - 2",    EnergyBalance(-2.499) ,  Pence(-249))
  )

  override def suitesStream: Stream[IO,RTest[Unit]] = testData
    .covary[IO]
    .map(x => test(x.scenarioName)(
        expect(roundInFavourOfCustomer(x.energyBalance) == x.expectedResult)
    ))
    .timeout(5.seconds)

}