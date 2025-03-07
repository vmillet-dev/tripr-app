package com.adsearch.infrastructure.web.controller

import com.adsearch.application.port.AdSearchUseCase
import com.adsearch.domain.model.Ad
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import com.adsearch.infrastructure.web.dto.SearchRequestDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class AdSearchControllerTest {
    
    private lateinit var adSearchUseCase: AdSearchUseCase
    private lateinit var adSearchController: AdSearchController
    
    @BeforeEach
    fun setUp() {
        adSearchUseCase = mockk()
        adSearchController = AdSearchController(adSearchUseCase, Dispatchers.Unconfined)
    }
    
    @Test
    fun `should return search results`() = runBlocking {
        // Given
        val ad = Ad(
            id = 1L,
            title = "Test Ad",
            description = "Test Description",
            sourceId = "1",
            sourceName = "TestSource"
        )
        
        val searchResult = SearchResult(
            ads = listOf(ad),
            totalCount = 1,
            sources = listOf("TestSource")
        )
        
        val searchRequest = SearchRequestDto(query = "test")
        coEvery { adSearchUseCase.searchAdsAcrossSources(any()) } returns searchResult
        
        // When
        val response = adSearchController.searchAds(searchRequest)
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body!!
        assertEquals(1, responseBody.ads.size)
        assertEquals(1, responseBody.totalCount)
        assertEquals(listOf("TestSource"), responseBody.sources)
        assertEquals(1, responseBody.page)
        assertEquals(20, responseBody.pageSize)
        assertEquals(1, responseBody.totalPages)
        
        coVerify { adSearchUseCase.searchAdsAcrossSources(match { 
            it.query == "test" && it.limit == 20 && it.offset == 0
        }) }
    }
    
    @Test
    fun `should return available sources`() = runBlocking {
        // Given
        val sources = listOf("Source1", "Source2")
        coEvery { adSearchUseCase.getAvailableSources() } returns sources
        
        // When
        val response = adSearchController.getAvailableSources()
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        val responseBody = response.body!!
        assertEquals(sources, responseBody["sources"])
        
        coVerify { adSearchUseCase.getAvailableSources() }
    }
}
