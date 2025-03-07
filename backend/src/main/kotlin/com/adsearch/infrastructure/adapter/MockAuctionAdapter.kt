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
 * Mock adapter for an auction service
 */
@Component
class MockAuctionAdapter : AdSearchAdapter() {
    
    private val logger = LoggerFactory.getLogger(MockAuctionAdapter::class.java)
    
    override val sourceName: String = "MockAuction"
    
    override suspend fun searchAds(criteria: SearchCriteria): SearchResult {
        logger.info("Searching ads from $sourceName with criteria: $criteria")
        
        // Simulate network delay
        Thread.sleep(Random.nextLong(200, 400))
        
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
        val categories = listOf("Antiques", "Art", "Collectibles", "Jewelry", "Memorabilia")
        val selectedCategory = category ?: categories.random()
        
        return (1..limit).map {
            val title = if (query.isNotBlank()) {
                "Auction: $query - Lot #$it"
            } else {
                "Auction $selectedCategory lot #$it"
            }
            
            Ad(
                id = 0L,
                title = title,
                description = "This is an auction listing for $title. Bidding ends soon!",
                price = Random.nextDouble(50.0, 5000.0),
                currency = "USD",
                imageUrl = "https://auction.example.com/lots/${Random.nextInt(100000, 999999)}.jpg",
                sourceId = "AU-${Random.nextInt(10000, 99999)}",
                sourceName = sourceName,
                externalUrl = "https://auction.example.com/lots/${Random.nextInt(100000, 999999)}",
                createdAt = LocalDateTime.now().minusDays(Random.nextLong(0, 7)),
                category = selectedCategory,
                location = listOf("London", "Paris", "New York", "Tokyo", "Berlin").random(),
                tags = listOf("auction", "bidding", "rare", "collectible").shuffled().take(Random.nextInt(1, 4))
            )
        }
    }
}
