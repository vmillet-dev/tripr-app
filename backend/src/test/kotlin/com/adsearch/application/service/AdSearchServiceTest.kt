package com.adsearch.application.service

import com.adsearch.domain.exception.SourceErrorException
import com.adsearch.domain.model.Ad
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import com.adsearch.domain.port.service.AdSearchPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AdSearchServiceTest {
    
    private lateinit var source1: AdSearchPort
    private lateinit var source2: AdSearchPort
    private lateinit var adSearchService: AdSearchService
    
    @BeforeEach
    fun setUp() {
        source1 = mockk<AdSearchPort>()
        source2 = mockk<AdSearchPort>()
        
        every { source1.sourceName } returns "Source1"
        every { source2.sourceName } returns "Source2"
        
        adSearchService = AdSearchService(listOf(source1, source2))
    }
    
    @Test
    fun `should return empty result when no sources are available`() = runBlocking {
        // Given
        coEvery { source1.isAvailable() } returns false
        coEvery { source2.isAvailable() } returns false
        
        // When
        val criteria = SearchCriteria(query = "test")
        val result = adSearchService.searchAdsAcrossSources(criteria)
        
        // Then
        assertEquals(0, result.ads.size)
        assertEquals(0, result.totalCount)
        assertEquals(emptyList<String>(), result.sources)
        
        coVerify { source1.isAvailable() }
        coVerify { source2.isAvailable() }
    }
    
    @Test
    fun `should aggregate results from all available sources`() = runBlocking {
        // Given
        val ad1 = Ad(
            id = 1L,
            title = "Ad 1",
            description = "Description 1",
            sourceId = "1",
            sourceName = "Source1"
        )
        
        val ad2 = Ad(
            id = 2L,
            title = "Ad 2",
            description = "Description 2",
            sourceId = "2",
            sourceName = "Source2"
        )
        
        val result1 = SearchResult(
            ads = listOf(ad1),
            totalCount = 1,
            sources = listOf("Source1")
        )
        
        val result2 = SearchResult(
            ads = listOf(ad2),
            totalCount = 1,
            sources = listOf("Source2")
        )
        
        coEvery { source1.isAvailable() } returns true
        coEvery { source2.isAvailable() } returns true
        
        val criteria = SearchCriteria(query = "test")
        coEvery { source1.searchAds(criteria) } returns result1
        coEvery { source2.searchAds(criteria) } returns result2
        
        // When
        val result = adSearchService.searchAdsAcrossSources(criteria)
        
        // Then
        assertEquals(2, result.ads.size)
        assertEquals(2, result.totalCount)
        assertEquals(listOf("Source1", "Source2"), result.sources)
        
        assertTrue(result.ads.any { it.id == ad1.id })
        assertTrue(result.ads.any { it.id == ad2.id })
        
        coVerify { source1.isAvailable() }
        coVerify { source2.isAvailable() }
        coVerify { source1.searchAds(criteria) }
        coVerify { source2.searchAds(criteria) }
    }
    
    @Test
    fun `should handle source errors and propagate exceptions`() = runBlocking {
        // Given
        coEvery { source1.isAvailable() } returns true
        coEvery { source2.isAvailable() } returns true
        
        val criteria = SearchCriteria(query = "test")
        coEvery { source1.searchAds(criteria) } throws RuntimeException("Source error")
        
        // When/Then
        assertThrows<SourceErrorException> {
            runBlocking {
                adSearchService.searchAdsAcrossSources(criteria)
            }
        }
        
        coVerify { source1.isAvailable() }
        coVerify { source2.isAvailable() }
        coVerify { source1.searchAds(criteria) }
    }
    
    @Test
    fun `should return only available sources`() = runBlocking {
        // Given
        coEvery { source1.isAvailable() } returns true
        coEvery { source2.isAvailable() } returns false
        
        // When
        val sources = adSearchService.getAvailableSources()
        
        // Then
        assertEquals(listOf("Source1"), sources)
        
        coVerify { source1.isAvailable() }
        coVerify { source2.isAvailable() }
    }
}
