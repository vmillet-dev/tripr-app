package com.adsearch.integration

import com.adsearch.infrastructure.web.dto.PasswordResetRequestDto
import com.adsearch.infrastructure.web.dto.RegisterRequestDto
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
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.annotation.DirtiesContext

/**
 * Integration tests for the PasswordResetController.
 * 
 * These tests verify that the password reset endpoints work correctly
 * by making actual HTTP requests to the running application.
 * 
 * The @DirtiesContext annotation ensures that the Spring context is reset
 * before each test method to provide a clean testing environment.
 */
@Tag("integration")
@DisplayName("Password Reset Controller Integration Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PasswordResetControllerIntegrationTest : AbstractIntegrationTest() {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    /**
     * Sets up a test user for password reset tests.
     * This method runs before each test to ensure a valid user exists.
     */
    @BeforeEach
    override fun setUp() {
        super.setUp()
        
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
    @DisplayName("POST /api/auth/password/reset-request should initiate password reset")
    fun shouldRequestPasswordReset() {
        // Given: Valid username for password reset
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val request = PasswordResetRequestDto(
            username = "testuser"
        )
        
        val entity = HttpEntity(request, headers)
        
        // When: Requesting password reset
        val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
            "http://localhost:$port/api/auth/password/reset-request",
            entity,
            Map::class.java
        )
        
        // Then: Response should be successful and contain confirmation message
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        
        // Verify the exact message content
        assertThat(response.body!!["message"]).isEqualTo("If the username exists, a password reset email has been sent")
        
        // Verify no other fields are present in the response
        assertThat(response.body!!.keys).containsExactly("message")
        
        // Verify email would have been sent (in a real scenario)
        // This is a security feature - we always return OK even if username doesn't exist
    }
}
