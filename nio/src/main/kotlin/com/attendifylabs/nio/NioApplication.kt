package com.attendifylabs.blocking

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@SpringBootApplication
class BlockingApplication

fun main() {
    runApplication<BlockingApplication>()
}

@Controller
@RequestMapping("ramen")
class RamenController(
    private val webClient: WebClient = WebClient.create(),
    private val databaseClient: DatabaseClient
) {

    @PostMapping
    suspend fun orderRamen() =
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
                .bind(RAMEN_ORDER, this)
                .fetch().rowsUpdated().awaitSingle()
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

const val CREATE_RAMEN_ORDER = "INSERT INTO ramen_order(ramenOrder) VALUES (:ramenOrder)"
const val RAMEN_ORDER = "ramenOrder"

const val NOODLES_URL = "null"
const val CHICKEN_URL = "null"
const val EGG_URL = "null"
const val SCALLION_URL = "null"
const val SOYSAUSE_URL = "null"
const val BROTH_URL = "null"
const val HOTSAUCE_URL = "null"

