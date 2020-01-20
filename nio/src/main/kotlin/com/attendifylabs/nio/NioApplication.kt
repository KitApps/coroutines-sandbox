package com.attendifylabs.nio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NioApplication

fun main(args: Array<String>) {
	runApplication<NioApplication>(*args)
}
