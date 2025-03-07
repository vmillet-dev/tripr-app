package com.adsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AdSearchApplication

fun main(args: Array<String>) {
    runApplication<AdSearchApplication>(*args)
}
