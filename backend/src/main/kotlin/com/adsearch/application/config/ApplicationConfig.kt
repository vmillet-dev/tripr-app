package com.adsearch.application.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * Application configuration for beans used across the application
 */
@Configuration
class ApplicationConfig {
    
    @Bean
    fun webClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}
