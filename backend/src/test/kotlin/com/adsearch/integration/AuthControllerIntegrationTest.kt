package com.adsearch.integration

import com.adsearch.infrastructure.repository.jpa.UserJpaRepository
import com.adsearch.infrastructure.repository.entity.UserEntity
import com.adsearch.infrastructure.web.dto.AuthRequestDto
import org.junit.jupiter.api.BeforeEach
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
import org.springframework.security.crypto.password.PasswordEncoder

class AuthControllerIntegrationTest : AbstractIntegrationTest() {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    @Autowired
    private lateinit var userRepository: UserJpaRepository
    
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    
    @BeforeEach
    fun setup() {
        // Clear any existing users
        userRepository.deleteAll()
        
        // Create a test user for authentication
        val user = UserEntity(
            username = "user",
            password = passwordEncoder.encode("password"),
            roles = mutableListOf("USER")
        )
        userRepository.save(user)
    }
    
    @Test
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
