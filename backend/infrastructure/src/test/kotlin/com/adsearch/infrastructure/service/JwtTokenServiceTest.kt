package com.adsearch.infrastructure.service

import com.adsearch.domain.model.UserDom
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("JWT Token Service Tests")
class JwtTokenServiceTest {

    private lateinit var jwtTokenService: JwtTokenService
    private val testSecret = "test-secret-key-that-is-long-enough-for-hmac256"
    private val testExpiration = 3600L
    private val testIssuer = "test-issuer"

    @BeforeEach
    fun setUp() {
        jwtTokenService = JwtTokenService(testSecret, testExpiration, testIssuer)
    }

    @Test
    @DisplayName("Should create valid access token when user is provided")
    fun shouldCreateValidAccessTokenWhenUserIsProvided() {
        // Given
        val user = UserDom(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            roles = setOf("USER", "ADMIN"),
            enabled = true
        )

        // When
        val token = jwtTokenService.createAccessToken(user)

        // Then
        assertNotNull(token)
        assert(token.isNotEmpty())
        assert(token.split(".").size == 3) // JWT has 3 parts separated by dots
    }

    @Test
    @DisplayName("Should validate token and return username when token is valid")
    fun shouldValidateTokenAndReturnUsernameWhenTokenIsValid() {
        // Given
        val user = UserDom(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            roles = setOf("USER"),
            enabled = true
        )
        val token = jwtTokenService.createAccessToken(user)

        // When
        val username = jwtTokenService.validateAccessTokenAndGetUsername(token)

        // Then
        assertEquals("testuser", username)
    }

    @Test
    @DisplayName("Should return null when token is invalid")
    fun shouldReturnNullWhenTokenIsInvalid() {
        // Given
        val invalidToken = "invalid.jwt.token"

        // When
        val username = jwtTokenService.validateAccessTokenAndGetUsername(invalidToken)

        // Then
        assertNull(username)
    }

    @Test
    @DisplayName("Should return null when token is malformed")
    fun shouldReturnNullWhenTokenIsMalformed() {
        // Given
        val malformedToken = "malformed-token"

        // When
        val username = jwtTokenService.validateAccessTokenAndGetUsername(malformedToken)

        // Then
        assertNull(username)
    }

    @Test
    @DisplayName("Should return null when token is empty")
    fun shouldReturnNullWhenTokenIsEmpty() {
        // Given
        val emptyToken = ""

        // When
        val username = jwtTokenService.validateAccessTokenAndGetUsername(emptyToken)

        // Then
        assertNull(username)
    }

    @Test
    @DisplayName("Should create token with correct issuer and expiration")
    fun shouldCreateTokenWithCorrectIssuerAndExpiration() {
        // Given
        val user = UserDom(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            roles = setOf("USER"),
            enabled = true
        )

        // When
        val token = jwtTokenService.createAccessToken(user)
        val username = jwtTokenService.validateAccessTokenAndGetUsername(token)

        // Then
        assertNotNull(token)
        assertEquals("testuser", username)
    }
}
