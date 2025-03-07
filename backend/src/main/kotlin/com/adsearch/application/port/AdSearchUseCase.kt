package com.adsearch.application.port

import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult

/**
 * Primary port for the application layer that defines the ad search use case
 */
interface AdSearchUseCase {
    /**
     * Search for ads across all available sources based on the provided criteria
     */
    suspend fun searchAdsAcrossSources(criteria: SearchCriteria): SearchResult
    
    /**
     * Get a list of all available ad sources
     */
    suspend fun getAvailableSources(): List<String>
}
