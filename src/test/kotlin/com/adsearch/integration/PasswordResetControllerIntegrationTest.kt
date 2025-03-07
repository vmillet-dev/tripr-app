package com.adsearch.integration

import com.adsearch.infrastructure.web.dto.PasswordResetRequestDto
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

class PasswordResetControllerIntegrationTest : AbstractIntegrationTest() {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    @Test
    @Disabled("Temporarily disabled until email service is properly set up in test environment")
    fun `should request password reset`() {
        // Given
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        
        val request = PasswordResetRequestDto(
            username = "user"
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
        assertNotNull(response.body!!["message"])
    }
}
