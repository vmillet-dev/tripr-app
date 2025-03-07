package com.adsearch.integration

import com.adsearch.infrastructure.web.dto.AuthRequestDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled

class AuthControllerIntegrationTest : AbstractIntegrationTest() {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    @Test
    @Disabled("Temporarily disabled until user authentication is properly set up in test environment")
    fun `should login with valid credentials`() {
        // Given
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val request = AuthRequestDto(
            username = "user",
            password = "password"
        )
        
        val entity = HttpEntity(request, headers)
        
        // When
        val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
            "http://localhost:$port/api/auth/login",
            entity,
            Map::class.java
        )
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body!!["accessToken"])
        assertEquals("user", response.body!!["username"])
    }
}
