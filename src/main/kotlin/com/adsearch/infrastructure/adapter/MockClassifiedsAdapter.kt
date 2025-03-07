package com.adsearch.infrastructure.adapter

import com.adsearch.domain.model.Ad
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

/**
 * Mock adapter for a classified ads service
 */
@Component
class MockClassifiedsAdapter : AdSearchAdapter() {
    
    private val logger = LoggerFactory.getLogger(MockClassifiedsAdapter::class.java)
    
    override val sourceName: String = "MockClassifieds"
    
    override suspend fun searchAds(criteria: SearchCriteria): SearchResult {
        logger.info("Searching ads from $sourceName with criteria: $criteria")
        
        // Simulate network delay
        Thread.sleep(Random.nextLong(100, 300))
        
        val query = criteria.query ?: ""
        val category = criteria.category
        
        // Generate mock ads based on search criteria
        val ads = generateMockAds(query, category, criteria.limit)
        
        // Filter by price if specified
        val filteredAds = ads.filter { ad ->
            (criteria.minPrice == null || (ad.price ?: 0.0) >= criteria.minPrice) &&
            (criteria.maxPrice == null || (ad.price ?: Double.MAX_VALUE) <= criteria.maxPrice)
        }
        
        logger.info("Found ${filteredAds.size} ads from $sourceName")
        
        return SearchResult(
            ads = filteredAds,
            totalCount = filteredAds.size,
            sources = listOf(sourceName)
        )
    }
    
    private fun generateMockAds(query: String, category: String?, limit: Int): List<Ad> {
        val categories = listOf("Electronics", "Vehicles", "Real Estate", "Jobs", "Services")
        val selectedCategory = category ?: categories.random()
        
        return (1..limit).map {
            val title = if (query.isNotBlank()) {
                "Ad for $query - Item $it"
            } else {
                "$selectedCategory item $it"
            }
            
            Ad(
                id = UUID.randomUUID(),
                title = title,
                description = "This is a mock ad for $title with detailed description",
                price = Random.nextDouble(10.0, 1000.0),
                currency = "USD",
                imageUrl = "https://example.com/images/${UUID.randomUUID()}.jpg",
                sourceId = "MC-${Random.nextInt(10000, 99999)}",
                sourceName = sourceName,
                externalUrl = "https://mockclassifieds.example.com/ads/${UUID.randomUUID()}",
                createdAt = LocalDateTime.now().minusDays(Random.nextLong(0, 30)),
                category = selectedCategory,
                location = listOf("New York", "Los Angeles", "Chicago", "Houston", "Phoenix").random(),
                tags = listOf("sale", "new", "used", "premium").shuffled().take(Random.nextInt(1, 4))
            )
        }
    }
}
