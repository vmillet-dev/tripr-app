package com.adsearch.infrastructure.adapter

import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import com.adsearch.domain.port.AdSearchPort

/**
 * Base abstract class for ad search adapters that implements common functionality
 */
abstract class AdSearchAdapter : AdSearchPort {
    
    /**
     * Default implementation for checking if the source is available
     * Subclasses can override this method to provide custom availability checks
     */
    override suspend fun isAvailable(): Boolean {
        return true
    }
    
    /**
     * Template method to be implemented by concrete adapters
     */
    abstract override suspend fun searchAds(criteria: SearchCriteria): SearchResult
}
