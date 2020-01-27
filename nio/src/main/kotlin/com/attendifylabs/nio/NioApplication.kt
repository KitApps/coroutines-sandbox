package com.attendifylabs.nio

import com.fasterxml.jackson.databind.ObjectMapper
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.Duration

@SpringBootApplication
@ConfigurationProperties(prefix = "spring.datasource")
class NioApplication {
    lateinit var username: String
    lateinit var password: String
    lateinit var dbname: String
    lateinit var host: String
    lateinit var port: String
    @Bean
    fun connectionFactory(): ConnectionFactory = PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host(host)
            .port(port.toInt())
            .database(dbname)
            .username(username)
            .password(password)
            .connectTimeout(Duration.ofSeconds(10))
            .build()
    )
}

fun main() {
    runApplication<NioApplication>()
}

@RestController
@RequestMapping("ramen")
class RamenController(
    private val webClient: WebClient = WebClient.create(),
    private val connectionFactory: ConnectionFactory,
    private val databaseClient: DatabaseClient = DatabaseClient.create(connectionFactory),
    private val objectMapper: ObjectMapper

) {

    @PostMapping
    suspend fun orderRamen() = coroutineScope {
        RamenOrder(
            noodles = webClient.get().uri(NOODLES_URL).awaitExchange().awaitBody(),
            broth = webClient.get().uri(BROTH_URL).awaitExchange().awaitBody(),
            chicken = webClient.get().uri(CHICKEN_URL).awaitExchange().awaitBody(),
            egg = webClient.get().uri(EGG_URL).awaitExchange().awaitBody(),
            scallion = webClient.get().uri(SCALLION_URL).awaitExchange().awaitBody(),
            soySauce = webClient.get().uri(SOYSAUSE_URL).awaitExchange().awaitBody(),
            hotSauce = webClient.get().uri(HOTSAUCE_URL).awaitExchange().awaitBody()
        ).apply {
            databaseClient.execute(CREATE_RAMEN_ORDER)
                .bind(RAMEN_ORDER, objectMapper.writeValueAsString(this))
                .fetch().rowsUpdated().awaitSingle()
        }
    }
}

data class Noodles(val content: List<String>)
data class Broth(val meatType: String)
data class Chicken(val amount: String)
data class Egg(val color: String)
data class Scallion(val amount: String)
data class SoySauce(val name: String)
class HotSauce(val isPresent: Boolean)
data class RamenOrder(
    val noodles: Noodles,
    val broth: Broth,
    val chicken: Chicken,
    val egg: Egg,
    val scallion: Scallion,
    val soySauce: SoySauce,
    val hotSauce: HotSauce
)

const val CREATE_RAMEN_ORDER = "INSERT INTO ramen_order(ramen_order) VALUES (to_json(:ramenOrder))"
const val RAMEN_ORDER = "ramenOrder"

const val NOODLES_URL = "https://blocking.s3.amazonaws.com/noodles.json"
const val CHICKEN_URL = "https://blocking.s3.amazonaws.com/chicken.json"
const val EGG_URL = "https://blocking.s3.amazonaws.com/egg.json"
const val SCALLION_URL = "https://blocking.s3.amazonaws.com/scallion.json"
const val SOYSAUSE_URL = "https://blocking.s3.amazonaws.com/soysauce.json"
const val BROTH_URL = "https://blocking.s3.amazonaws.com/broth.json"
const val HOTSAUCE_URL = "https://blocking.s3.amazonaws.com/hotsauce.json"

