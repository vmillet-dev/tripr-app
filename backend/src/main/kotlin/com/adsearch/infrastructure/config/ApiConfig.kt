package com.adsearch.infrastructure.config

import com.adsearch.infrastructure.adapter.ApiKeyRotator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for API-related beans
 */
@Configuration
class ApiConfig {
    
    @Value("\${api.keys:}")
    private lateinit var apiKeysString: String
    
    /**
     * Create an ApiKeyRotator bean with the configured API keys
     */
    @Bean
    fun apiKeyRotator(): ApiKeyRotator {
        val apiKeys = if (apiKeysString.isBlank()) {
            emptyList()
        } else {
            apiKeysString.split(",").map { it.trim() }
        }
        
        return ApiKeyRotator(apiKeys)
    }
}
