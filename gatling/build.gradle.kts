plugins {
    scala
    id("com.github.lkishalmi.gatling") version "3.0.2"
}

dependencies {
    implementation("org.scala-lang:scala-library:2.12.8")
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.1.1")
    implementation("io.gatling:gatling-test-framework:3.1.1")
}

val simulationName: String? by project
val defaultSimulationName: String = "SIMULATION"
val simulationClasses = mapOf(
    "SIMULATION" to listOf("com.attendify.sandbox.simulation.RamenLoadTestSimulation")
)

fun simulationsToRun(simulationName: String): List<String> = simulationClasses[simulationName].orEmpty()

gatling {
    toolVersion = "3.1.1"
    jvmArgs = listOf("-server", "-Xms4G", "-Xmx4G")
    simulations = simulationsToRun(simulationName ?: defaultSimulationName)
}

repositories {
    mavenCentral()
}
