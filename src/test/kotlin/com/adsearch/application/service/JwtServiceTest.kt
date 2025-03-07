package com.adsearch.application.service

import com.adsearch.domain.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtServiceTest {
    
    private lateinit var jwtService: JwtService
    private val secret = "testSecretKeyThatShouldBeAtLeast32CharactersLong"
    private val expiration = 3600000L
    private val issuer = "test-issuer"
    
    @BeforeEach
    fun setUp() {
        jwtService = JwtService(secret, expiration, issuer)
    }
    
    @Test
    fun `should generate valid token`() {
        // Given
        val user = User(
            id = 1L,
            username = "testuser",
            password = "password",
            roles = mutableListOf("USER")
        )
        
        // When
        val token = jwtService.generateToken(user)
        
        // Then
        assertTrue(jwtService.validateToken(token))
        assertEquals(user.id, jwtService.getUserIdFromToken(token))
        assertEquals(user.username, jwtService.getUsernameFromToken(token))
        assertEquals(user.roles, jwtService.getRolesFromToken(token))
    }
    
    @Test
    fun `should return false for invalid token`() {
        // Given
        val invalidToken = "invalid.token.string"
        
        // When/Then
        assertFalse(jwtService.validateToken(invalidToken))
    }
    
    @Test
    fun `should extract claims from token`() {
        // Given
        val userId = 1L
        val username = "testuser"
        val roles = mutableListOf("USER", "ADMIN")
        
        val user = User(
            id = userId,
            username = username,
            password = "password",
            roles = roles
        )
        
        // When
        val token = jwtService.generateToken(user)
        
        // Then
        assertEquals(userId, jwtService.getUserIdFromToken(token))
        assertEquals(username, jwtService.getUsernameFromToken(token))
        assertEquals(roles, jwtService.getRolesFromToken(token))
    }
}
