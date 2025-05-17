package com.adsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import com.adsearch.infrastructure.config.WebMvcConfig
import com.adsearch.infrastructure.config.SecurityConfig

@SpringBootApplication
@ComponentScan(basePackages = ["com.adsearch"])
@EnableJpaRepositories(basePackages = ["com.adsearch.infrastructure.adapter.out.persistence.jpa"])
@Import(WebMvcConfig::class, SecurityConfig::class)
class AdSearchApplication

fun main(args: Array<String>) {
    runApplication<AdSearchApplication>(*args)
}
