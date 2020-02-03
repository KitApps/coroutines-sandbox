package com.attendify.sandbox.simulation

import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.RampOpenInjection
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

/**
  * Test intended to find out pixel-api throughput
  */
class RamenLoadTestSimulation extends Simulation {

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrls("some_url")
    .headers(simulationHeaders)

  val happyPathScenario: ScenarioBuilder = scenario("Normal user tracking activity").exec(post)

  private val userInjection: RampOpenInjection = rampUsers(usersNumber) during (rampDuringSeconds seconds)

  setUp(happyPathScenario.inject(userInjection)).maxDuration(testDurationSeconds seconds).protocols(httpProtocol)


  val rampDuringSeconds = 300
  val usersNumber = 100
  val testDurationSeconds = 300L
  val simulationHeaders: Map[String, String] = Map(
    "Content-Type" -> "application/json;charset=UTF-8",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val post: ChainBuilder =
    exec(
      http("Init")
        .post("/init"))
}
