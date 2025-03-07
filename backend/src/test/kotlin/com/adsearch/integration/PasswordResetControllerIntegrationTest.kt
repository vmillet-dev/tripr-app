package com.adsearch.integration

import com.adsearch.infrastructure.web.dto.PasswordResetRequestDto
import com.adsearch.infrastructure.web.dto.RegisterRequestDto
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
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.annotation.DirtiesContext

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PasswordResetControllerIntegrationTest : AbstractIntegrationTest() {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    @BeforeEach
    fun setup() {
        // Create a test user for password reset
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val registerRequest = RegisterRequestDto(
            username = "testuser",
            password = "password",
            email = "testuser@example.com"
        )
        
        try {
            restTemplate.postForEntity(
                "http://localhost:$port/api/auth/register",
                HttpEntity(registerRequest, headers),
                Map::class.java
            )
        } catch (e: Exception) {
            // If registration fails (e.g., user already exists), log but continue
            println("User registration failed, likely already exists: ${e.message}")
        }
    }
    
    @Test
    fun `should request password reset`() {
        // Given
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val request = PasswordResetRequestDto(
            username = "testuser"
        )
        
        val entity = HttpEntity(request, headers)
        
        // When
        val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
            "http://localhost:$port/api/auth/password/reset-request",
            entity,
            Map::class.java
        )
        
        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals("If the username exists, a password reset email has been sent", response.body!!["message"])
    }
}
