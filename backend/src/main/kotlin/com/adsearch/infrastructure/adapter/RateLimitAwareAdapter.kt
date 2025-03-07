package com.adsearch.infrastructure.adapter

import com.adsearch.domain.exception.SourceUnavailableException
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import org.slf4j.LoggerFactory

/**
 * Abstract adapter that handles API key rotation for rate limits
 */
abstract class RateLimitAwareAdapter(
    private val apiKeyRotator: ApiKeyRotator
) : AdSearchAdapter() {
    
    private val logger = LoggerFactory.getLogger(RateLimitAwareAdapter::class.java)
    
    /**
     * Search for ads with automatic API key rotation on rate limit errors
     */
    override suspend fun searchAds(criteria: SearchCriteria): SearchResult {
        var attempts = 0
        val maxAttempts = 3
        
        while (attempts < maxAttempts) {
            val apiKey = apiKeyRotator.getNextApiKey()
            
            if (apiKey == null) {
                logger.error("All API keys have been exhausted for source: $sourceName")
                throw SourceUnavailableException(sourceName, 
                    Exception("All API keys have been exhausted"))
            }
            
            try {
                return executeSearch(criteria, apiKey)
            } catch (e: Exception) {
                attempts++
                
                if (isRateLimitError(e)) {
                    logger.warn("Rate limit exceeded for source: $sourceName, marking key as exhausted")
                    apiKeyRotator.markKeyExhausted(apiKey)
                    
                    if (attempts < maxAttempts) {
                        logger.info("Retrying with a different API key (attempt $attempts of $maxAttempts)")
                        continue
                    }
                }
                
                logger.error("Error searching ads from source: $sourceName", e)
                throw e
            }
        }
        
        throw SourceUnavailableException(sourceName, 
            Exception("Failed to search ads after $maxAttempts attempts"))
    }
    
    /**
     * Execute the actual search with the provided API key
     * To be implemented by concrete adapters
     */
    protected abstract suspend fun executeSearch(criteria: SearchCriteria, apiKey: String): SearchResult
    
    /**
     * Check if the exception is due to a rate limit error
     * To be implemented by concrete adapters
     */
    protected abstract fun isRateLimitError(exception: Exception): Boolean
}
