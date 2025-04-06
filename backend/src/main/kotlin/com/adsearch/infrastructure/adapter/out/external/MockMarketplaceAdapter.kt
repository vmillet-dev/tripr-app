package com.adsearch.infrastructure.adapter.out.external

import com.adsearch.domain.model.Ad
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * Mock adapter for a marketplace service
 */
@Component
class MockMarketplaceAdapter : AdSearchAdapter() {

    private val logger = LoggerFactory.getLogger(MockMarketplaceAdapter::class.java)

    override val sourceName: String = "MockMarketplace"

    override suspend fun searchAds(criteria: SearchCriteria): SearchResult {
        logger.info("Searching ads from $sourceName with criteria: $criteria")

        // Simulate network delay
        Thread.sleep(Random.nextLong(150, 350))

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
        val categories = listOf("Home & Garden", "Fashion", "Electronics", "Sports", "Collectibles")
        val selectedCategory = category ?: categories.random()

        return (1..limit).map {
            val title = if (query.isNotBlank()) {
                "Marketplace: $query (Item $it)"
            } else {
                "Marketplace $selectedCategory item $it"
            }

            Ad(
                id = 0L,
                title = title,
                description = "This is a marketplace listing for $title. Great condition, must see!",
                price = Random.nextDouble(5.0, 500.0),
                currency = "USD",
                imageUrl = "https://marketplace.example.com/images/${Random.nextInt(100000, 999999)}.jpg",
                sourceId = "MP-${Random.nextInt(10000, 99999)}",
                sourceName = sourceName,
                externalUrl = "https://marketplace.example.com/items/${Random.nextInt(100000, 999999)}",
                createdAt = LocalDateTime.now().minusDays(Random.nextLong(0, 14)),
                category = selectedCategory,
                location = listOf("Boston", "Seattle", "Austin", "Denver", "Miami").random(),
                tags = listOf("marketplace", "verified", "local", "pickup").shuffled().take(Random.nextInt(1, 3))
            )
        }
    }
}
