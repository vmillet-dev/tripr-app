package com.adsearch.integration

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.assertj.core.api.Assertions.assertThat

/**
 * Integration tests for the AdSearchController.
 * 
 * These tests verify that the ad search endpoints work correctly
 * by making actual HTTP requests to the running application.
 */
@Tag("integration")
@DisplayName("Ad Search Controller Integration Tests")
class AdSearchControllerIntegrationTest : AbstractIntegrationTest() {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    @Test
    @DisplayName("GET /api/ads/sources should return available ad sources")
    fun shouldReturnAvailableSources() {
        // When: Requesting available sources
        val response: ResponseEntity<Map<*, *>> = restTemplate.getForEntity(
            "http://localhost:$port/api/ads/sources",
            Map::class.java
        )
        
        // Then: Response should be successful and contain sources
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!["sources"]).isNotNull
        assertThat(response.body!!["sources"]).isInstanceOf(List::class.java)
    }
    
    @Test
    @DisplayName("GET /api/ads/search should return search results")
    fun shouldReturnSearchResults() {
        // Given: Search request with query parameters
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        // When: Performing a search with query parameters
        val response: ResponseEntity<Map<*, *>> = restTemplate.getForEntity(
            "http://localhost:$port/api/ads/search?query=test&page=1&pageSize=10",
            Map::class.java
        )
        
        // Then: Response should be successful and contain search results
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        
        // Verify response structure contains expected keys
        val responseBody = response.body!!
        assertThat(responseBody.containsKey("ads")).isTrue()
        assertThat(responseBody.containsKey("totalCount")).isTrue()
        assertThat(responseBody.containsKey("sources")).isTrue()
        assertThat(responseBody.containsKey("page")).isTrue()
        assertThat(responseBody.containsKey("pageSize")).isTrue()
        assertThat(responseBody.containsKey("totalPages")).isTrue()
        
        // Verify pagination information
        assertThat(response.body!!["page"]).isNotNull
        assertThat(response.body!!["pageSize"]).isNotNull
        assertThat(response.body!!["totalPages"]).isNotNull
        
        // Verify sources information
        assertThat(response.body!!["sources"]).isInstanceOf(List::class.java)
    }
}
