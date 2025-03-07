package com.adsearch.infrastructure.adapter

import com.adsearch.domain.model.Ad
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.LocalDateTime
import java.util.UUID

/**
 * Example of a real-world adapter that would connect to an actual external API
 * This is just a skeleton and not actually used in the application
 */
//@Component // Commented out to use mock adapters instead
class RealWorldExampleAdapter(
    private val webClientBuilder: WebClient.Builder,
    private val apiKeyRotator: ApiKeyRotator,
    
    @Value("\${api.endpoints.example:https://api.example.com}")
    private val apiEndpoint: String
) : RateLimitAwareAdapter(apiKeyRotator) {
    
    private val logger = LoggerFactory.getLogger(RealWorldExampleAdapter::class.java)
    
    override val sourceName: String = "ExampleAPI"
    
    override suspend fun isAvailable(): Boolean {
        return try {
            val apiKey = apiKeyRotator.getNextApiKey() ?: return false
            
            webClientBuilder.build()
                .get()
                .uri("$apiEndpoint/health")
                .header("Authorization", "Bearer $apiKey")
                .retrieve()
                .toBodilessEntity()
                .block()
                
            true
        } catch (e: Exception) {
            logger.warn("Source $sourceName is unavailable", e)
            false
        }
    }
    
    override suspend fun executeSearch(criteria: SearchCriteria, apiKey: String): SearchResult {
        logger.info("Executing search on $sourceName with criteria: $criteria")
        
        // In a real implementation, this would call the actual API
        // This is just a placeholder
        
        // Example of how the API call might look:
        /*
        val response = webClientBuilder.build()
            .get()
            .uri { uriBuilder ->
                uriBuilder.path("$apiEndpoint/search")
                    .queryParam("q", criteria.query)
                    .queryParam("category", criteria.category)
                    .queryParam("min_price", criteria.minPrice)
                    .queryParam("max_price", criteria.maxPrice)
                    .queryParam("location", criteria.location)
                    .queryParam("limit", criteria.limit)
                    .queryParam("offset", criteria.offset)
                    .build()
            }
            .header("Authorization", "Bearer $apiKey")
            .retrieve()
            .bodyToMono(ExampleApiResponse::class.java)
            .block()
            
        return mapToSearchResult(response)
        */
        
        // For now, just return mock data
        return SearchResult(
            ads = listOf(
                Ad(
                    id = UUID.randomUUID(),
                    title = "Example API Ad",
                    description = "This is a placeholder for a real API response",
                    price = 99.99,
                    currency = "USD",
                    imageUrl = "https://example.com/image.jpg",
                    sourceId = "EX-12345",
                    sourceName = sourceName,
                    externalUrl = "https://example.com/ads/12345",
                    createdAt = LocalDateTime.now(),
                    category = "Example",
                    location = "Example City",
                    tags = listOf("example", "placeholder")
                )
            ),
            totalCount = 1,
            sources = listOf(sourceName)
        )
    }
    
    override fun isRateLimitError(exception: Exception): Boolean {
        return exception is WebClientResponseException && 
               (exception.statusCode == HttpStatus.TOO_MANY_REQUESTS || 
                exception.statusCode == HttpStatus.FORBIDDEN)
    }
    
    // Example of a data class that would map to the API response
    private data class ExampleApiResponse(
        val items: List<ExampleApiItem>,
        val total: Int,
        val page: Int,
        val pageSize: Int
    )
    
    private data class ExampleApiItem(
        val id: String,
        val title: String,
        val description: String,
        val price: Double,
        val currency: String,
        val imageUrl: String,
        val url: String,
        val createdAt: String,
        val category: String,
        val location: String,
        val tags: List<String>
    )
    
    // Example of mapping from API response to domain model
    private fun mapToSearchResult(response: ExampleApiResponse): SearchResult {
        val ads = response.items.map { item ->
            Ad(
                id = UUID.randomUUID(),
                title = item.title,
                description = item.description,
                price = item.price,
                currency = item.currency,
                imageUrl = item.imageUrl,
                sourceId = item.id,
                sourceName = sourceName,
                externalUrl = item.url,
                createdAt = LocalDateTime.parse(item.createdAt),
                category = item.category,
                location = item.location,
                tags = item.tags
            )
        }
        
        return SearchResult(
            ads = ads,
            totalCount = response.total,
            sources = listOf(sourceName)
        )
    }
}
