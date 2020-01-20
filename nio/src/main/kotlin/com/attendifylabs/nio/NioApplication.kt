package com.attendifylabs.blocking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@SpringBootApplication
class BlockingApplication

fun main() {
    runApplication<BlockingApplication>()
}

@Controller
@RequestMapping("ramen")
class RamenController(
    val jdbcTemplate: NamedParameterJdbcTemplate,
    val restTemplate: RestTemplate
) {

    @PostMapping
    fun orderRamen() =
        RamenOrder(
            noodles = restTemplate.getForObject(NOODLES_URL),
            broth = restTemplate.getForObject(BROTH_URL),
            chicken = restTemplate.getForObject(CHICKEN_URL),
            egg = restTemplate.getForObject(EGG_URL),
            scallion = restTemplate.getForObject(SCALLION_URL),
            soySauce = restTemplate.getForObject(SOYSAUSE_URL),
            hotSauce = restTemplate.getForObject(HOTSAUCE_URL)
        ).apply {
            jdbcTemplate.update(CREATE_RAMEN_ORDER, mapOf(RAMEN_ORDER to this))
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

