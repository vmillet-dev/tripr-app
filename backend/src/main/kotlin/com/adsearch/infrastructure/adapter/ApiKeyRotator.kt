package com.adsearch.infrastructure.adapter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

/**
 * Utility class for rotating through API keys to manage rate limits
 */
@Component
class ApiKeyRotator(
    private val apiKeys: List<String> = emptyList()
) {
    private val logger = LoggerFactory.getLogger(ApiKeyRotator::class.java)
    private val currentIndex = AtomicInteger(0)
    private val exhaustedKeys = mutableSetOf<String>()
    
    /**
     * Get the next available API key
     * @return The next API key or null if all keys are exhausted
     */
    @Synchronized
    fun getNextApiKey(): String? {
        if (apiKeys.isEmpty()) {
            logger.warn("No API keys configured")
            return null
        }
        
        if (exhaustedKeys.size == apiKeys.size) {
            logger.error("All API keys have been exhausted")
            return null
        }
        
        var attempts = 0
        while (attempts < apiKeys.size) {
            val index = currentIndex.getAndUpdate { (it + 1) % apiKeys.size }
            val key = apiKeys[index]
            
            if (key !in exhaustedKeys) {
                logger.debug("Using API key at index $index")
                return key
            }
            
            attempts++
        }
        
        logger.error("Failed to find a non-exhausted API key")
        return null
    }
    
    /**
     * Mark an API key as exhausted (reached rate limit)
     * @param apiKey The API key to mark as exhausted
     */
    @Synchronized
    fun markKeyExhausted(apiKey: String) {
        if (apiKey in apiKeys) {
            logger.warn("API key has been marked as exhausted: ${apiKey.take(5)}...")
            exhaustedKeys.add(apiKey)
        }
    }
    
    /**
     * Reset all exhausted keys
     */
    @Synchronized
    fun resetExhaustedKeys() {
        logger.info("Resetting all exhausted API keys")
        exhaustedKeys.clear()
    }
    
    /**
     * Check if all API keys are exhausted
     * @return true if all keys are exhausted, false otherwise
     */
    fun areAllKeysExhausted(): Boolean {
        return apiKeys.isNotEmpty() && exhaustedKeys.size == apiKeys.size
    }
}
