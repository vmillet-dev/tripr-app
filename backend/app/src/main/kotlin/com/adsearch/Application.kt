package com.adsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.adsearch"])
@EnableJpaRepositories(basePackages = ["com.adsearch.infrastructure.adapter.out.persistence.jpa"])
class AdSearchApplication

fun main(args: Array<String>) {
    runApplication<AdSearchApplication>(*args)
}
