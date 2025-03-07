package com.adsearch.domain.port

import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult

/**
 * Port (interface) for searching ads from external sources
 * This is a primary/driving port that will be implemented by adapters in the infrastructure layer
 */
interface AdSearchPort {
    /**
     * Name of the ad source
     */
    val sourceName: String
    
    /**
     * Search for ads based on the provided criteria
     */
    suspend fun searchAds(criteria: SearchCriteria): SearchResult
    
    /**
     * Check if the source is available
     */
    suspend fun isAvailable(): Boolean
}
