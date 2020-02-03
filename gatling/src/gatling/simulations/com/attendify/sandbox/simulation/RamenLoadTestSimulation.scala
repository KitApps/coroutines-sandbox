package com.attendify.sandbox.simulation

import com.attendify.sandbox.simulation.Configs._
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.{AtOnceOpenInjection, RampOpenInjection}
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.sys.SystemProperties
import scala.util.Try

/**
  * Test intended to find out pixel-api throughput
  */
class RamenLoadTestSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrls(serviceUrl)
    .headers(simulationHeaders)

  val post: ChainBuilder = exec(http("Order Ramen").post("/ramen").check(status.is(200)))

  val happyPathScenario: ScenarioBuilder = scenario("Normal user tracking activity").exec(post)

//  private val rampSingleUserStrategy: AtOnceOpenInjection = atOnceUsers(1)
  private val userInjection: RampOpenInjection = rampUsers(usersNumber) during (rampDuringSeconds seconds)

  setUp(happyPathScenario.inject(userInjection)).maxDuration(testDurationSeconds seconds).protocols(httpProtocol)

}

object Configs {

  val serviceUrl: String = SystemPropertiesUtil.getAsStringOrElse("SERVICE_INSTANCE", "")
  val rampDuringSeconds: Int = SystemPropertiesUtil.getAsIntOrElse("SIMULATION_DURATION", 300)

  val numberOfUsersPerSecond: Int = 10
  val usersNumber: Int = rampDuringSeconds * numberOfUsersPerSecond
  val testDurationSeconds: Int = rampDuringSeconds + 30
  val simulationHeaders: Map[String, String] = Map(
    "Content-Type" -> "application/json;charset=UTF-8",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")
}

object SystemPropertiesUtil {
  val systemProperties = new SystemProperties

  private def readProperty[A](propName: String, default: A)(f: String => A): A =
    Try(sys.env(propName)).map(f).getOrElse(default)

  def getAsStringOrElse(property: String, default: String): String = readProperty(property, default)(_.toString)

  def getAsIntOrElse(property: String, default: Int): Int = readProperty(property, default)(_.toInt)

}
