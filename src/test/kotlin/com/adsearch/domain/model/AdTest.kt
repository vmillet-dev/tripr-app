package com.adsearch.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class AdTest {
    
    @Test
    fun `should create ad with required fields`() {
        val sourceId = "test-source-id"
        val sourceName = "TestSource"
        val title = "Test Ad"
        val description = "Test Description"
        
        val ad = Ad(
            title = title,
            description = description,
            sourceId = sourceId,
            sourceName = sourceName
        )
        
        assertNotNull(ad.id)
        assertEquals(title, ad.title)
        assertEquals(description, ad.description)
        assertEquals(sourceId, ad.sourceId)
        assertEquals(sourceName, ad.sourceName)
        assertNotNull(ad.createdAt)
        assertEquals(emptyList<String>(), ad.tags)
    }
    
    @Test
    fun `should create ad with all fields`() {
        val id = UUID.randomUUID()
        val title = "Test Ad"
        val description = "Test Description"
        val price = 99.99
        val currency = "USD"
        val imageUrl = "https://example.com/image.jpg"
        val sourceId = "test-source-id"
        val sourceName = "TestSource"
        val externalUrl = "https://example.com/ad/123"
        val createdAt = LocalDateTime.now()
        val category = "Electronics"
        val location = "New York"
        val tags = listOf("test", "example")
        
        val ad = Ad(
            id = id,
            title = title,
            description = description,
            price = price,
            currency = currency,
            imageUrl = imageUrl,
            sourceId = sourceId,
            sourceName = sourceName,
            externalUrl = externalUrl,
            createdAt = createdAt,
            category = category,
            location = location,
            tags = tags
        )
        
        assertEquals(id, ad.id)
        assertEquals(title, ad.title)
        assertEquals(description, ad.description)
        assertEquals(price, ad.price)
        assertEquals(currency, ad.currency)
        assertEquals(imageUrl, ad.imageUrl)
        assertEquals(sourceId, ad.sourceId)
        assertEquals(sourceName, ad.sourceName)
        assertEquals(externalUrl, ad.externalUrl)
        assertEquals(createdAt, ad.createdAt)
        assertEquals(category, ad.category)
        assertEquals(location, ad.location)
        assertEquals(tags, ad.tags)
    }
}
