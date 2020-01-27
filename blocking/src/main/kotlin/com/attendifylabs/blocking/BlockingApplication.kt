package com.attendifylabs.blocking

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@SpringBootApplication
class BlockingApplication {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate =
        builder.build()
}

fun main() {
    runApplication<BlockingApplication>()
}

@RestController
@RequestMapping("ramen")
class RamenController(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
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
            jdbcTemplate.update(CREATE_RAMEN_ORDER, mapOf(RAMEN_ORDER to objectMapper.writeValueAsString(this)))
        }
}

data class Noodles(val content: List<String>)
data class Broth(val meatType: String)
data class Chicken(val amount: Short)
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

