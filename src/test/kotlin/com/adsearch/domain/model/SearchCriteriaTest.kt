package com.adsearch.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SearchCriteriaTest {
    
    @Test
    fun `should create search criteria with default values`() {
        val criteria = SearchCriteria()
        
        assertEquals(null, criteria.query)
        assertEquals(null, criteria.category)
        assertEquals(null, criteria.minPrice)
        assertEquals(null, criteria.maxPrice)
        assertEquals(null, criteria.location)
        assertEquals(SortOption.RELEVANCE, criteria.sortBy)
        assertEquals(20, criteria.limit)
        assertEquals(0, criteria.offset)
    }
    
    @Test
    fun `should create search criteria with custom values`() {
        val criteria = SearchCriteria(
            query = "test query",
            category = "Electronics",
            minPrice = 10.0,
            maxPrice = 100.0,
            location = "New York",
            sortBy = SortOption.PRICE_ASC,
            limit = 50,
            offset = 10
        )
        
        assertEquals("test query", criteria.query)
        assertEquals("Electronics", criteria.category)
        assertEquals(10.0, criteria.minPrice)
        assertEquals(100.0, criteria.maxPrice)
        assertEquals("New York", criteria.location)
        assertEquals(SortOption.PRICE_ASC, criteria.sortBy)
        assertEquals(50, criteria.limit)
        assertEquals(10, criteria.offset)
    }
}
