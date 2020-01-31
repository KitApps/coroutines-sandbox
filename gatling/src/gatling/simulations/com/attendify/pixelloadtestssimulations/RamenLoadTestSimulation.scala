package com.attendify.pixelloadtestssimulations

//import com.attendify.pixelloadtestssimulations.SimulationConfigs._
//import PixelApiActions.

import scala.concurrent.duration._

/**
  * Test intended to find out pixel-api throughput
  */
class RamenLoadTestSimulation /*extends Simulation*/ {

//  val feeder: SourceFeederBuilder[Any] = jsonFile(pixelApiFeederFilePath).circular.random
//
//  val httpProtocol: HttpProtocolBuilder = http
//    .baseUrls(pixelServiceUrls)
//    .headers(Common.simulationHeaders)
//
//  val happyPathScenario: ScenarioBuilder = scenario("Normal user tracking activity").feed(feeder)
//    .exec(init, pageVisit, instantTracks, userActivity, identify, reset)
//
//  val invalidRequestsScenario: ScenarioBuilder = scenario("Users with invalid track requests").feed(feeder).exec(init, invalidTracks)
//
//  private val userInjection: RampOpenInjection = rampUsers(usersNumber) during(rampDuringSeconds seconds)
//
//  private val hackerUsersNumber: Int = (usersNumber * 0.01).intValue()
//  private val hackerUserInjection: RampOpenInjection = rampUsers(hackerUsersNumber) during(rampDuringSeconds seconds)
//
//  setUp(List(
//    happyPathScenario.inject(userInjection),
//    invalidRequestsScenario.inject(hackerUserInjection)
//  )).maxDuration(testDurationSeconds seconds).protocols(httpProtocol)
////    .assertions(global.failedRequests.count.is(0))


  val simulationHeaders: Map[String, String] = Map(
    "Content-Type" -> "application/json;charset=UTF-8",
    "User-Agent" -> "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")
}
