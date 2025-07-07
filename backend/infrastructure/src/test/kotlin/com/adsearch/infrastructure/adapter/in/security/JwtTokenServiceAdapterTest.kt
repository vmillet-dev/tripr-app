package com.adsearch.infrastructure.adapter.`in`.security

import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.service.JwtTokenService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("JWT Token Service Adapter Tests")
class JwtTokenServiceAdapterTest {

    private lateinit var jwtTokenServiceAdapter: JwtTokenServiceAdapter
    private val jwtTokenService = mockk<JwtTokenService>()

    @BeforeEach
    fun setUp() {
        jwtTokenServiceAdapter = JwtTokenServiceAdapter(jwtTokenService)
    }

    @Test
    @DisplayName("Should create access token when user is provided")
    fun shouldCreateAccessTokenWhenUserIsProvided() {
        // Given
        val user = UserDom(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            roles = setOf("USER"),
            enabled = true
        )
        val expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token"

        every { jwtTokenService.createAccessToken(user) } returns expectedToken

        // When
        val actualToken = jwtTokenServiceAdapter.createAccessToken(user)

        // Then
        assertEquals(expectedToken, actualToken)
        verify(exactly = 1) { jwtTokenService.createAccessToken(user) }
    }

    @Test
    @DisplayName("Should delegate token creation to underlying service")
    fun shouldDelegateTokenCreationToUnderlyingService() {
        // Given
        val user = UserDom(
            id = 2L,
            username = "delegateuser",
            email = "delegate@example.com",
            password = "delegatepassword",
            roles = setOf("ADMIN"),
            enabled = true
        )
        val delegatedToken = "delegated.jwt.token"

        every { jwtTokenService.createAccessToken(user) } returns delegatedToken

        // When
        val result = jwtTokenServiceAdapter.createAccessToken(user)

        // Then
        assertNotNull(result)
        assertEquals(delegatedToken, result)
        verify(exactly = 1) { jwtTokenService.createAccessToken(user) }
    }

    @Test
    @DisplayName("Should validate token and return username when token is valid")
    fun shouldValidateTokenAndReturnUsernameWhenTokenIsValid() {
        // Given
        val validToken = "valid.jwt.token"
        val expectedUsername = "validuser"

        every { jwtTokenService.validateAccessTokenAndGetUsername(validToken) } returns expectedUsername

        // When
        val actualUsername = jwtTokenServiceAdapter.validateAccessTokenAndGetUsername(validToken)

        // Then
        assertEquals(expectedUsername, actualUsername)
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(validToken) }
    }

    @Test
    @DisplayName("Should return null when token is invalid")
    fun shouldReturnNullWhenTokenIsInvalid() {
        // Given
        val invalidToken = "invalid.jwt.token"

        every { jwtTokenService.validateAccessTokenAndGetUsername(invalidToken) } returns null

        // When
        val result = jwtTokenServiceAdapter.validateAccessTokenAndGetUsername(invalidToken)

        // Then
        assertNull(result)
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(invalidToken) }
    }

    @Test
    @DisplayName("Should delegate token validation to underlying service")
    fun shouldDelegateTokenValidationToUnderlyingService() {
        // Given
        val token = "delegation.test.token"
        val delegatedUsername = "delegateduser"

        every { jwtTokenService.validateAccessTokenAndGetUsername(token) } returns delegatedUsername

        // When
        val result = jwtTokenServiceAdapter.validateAccessTokenAndGetUsername(token)

        // Then
        assertEquals(delegatedUsername, result)
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(token) }
    }

    @Test
    @DisplayName("Should handle empty token correctly")
    fun shouldHandleEmptyTokenCorrectly() {
        // Given
        val emptyToken = ""

        every { jwtTokenService.validateAccessTokenAndGetUsername(emptyToken) } returns null

        // When
        val result = jwtTokenServiceAdapter.validateAccessTokenAndGetUsername(emptyToken)

        // Then
        assertNull(result)
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(emptyToken) }
    }

    @Test
    @DisplayName("Should create different tokens for different users")
    fun shouldCreateDifferentTokensForDifferentUsers() {
        // Given
        val user1 = UserDom(
            id = 1L,
            username = "user1",
            email = "user1@example.com",
            password = "password1",
            roles = setOf("USER"),
            enabled = true
        )
        val user2 = UserDom(
            id = 2L,
            username = "user2",
            email = "user2@example.com",
            password = "password2",
            roles = setOf("ADMIN"),
            enabled = true
        )
        val token1 = "token.for.user1"
        val token2 = "token.for.user2"

        every { jwtTokenService.createAccessToken(user1) } returns token1
        every { jwtTokenService.createAccessToken(user2) } returns token2

        // When
        val result1 = jwtTokenServiceAdapter.createAccessToken(user1)
        val result2 = jwtTokenServiceAdapter.createAccessToken(user2)

        // Then
        assertEquals(token1, result1)
        assertEquals(token2, result2)
        verify(exactly = 1) { jwtTokenService.createAccessToken(user1) }
        verify(exactly = 1) { jwtTokenService.createAccessToken(user2) }
    }

    @Test
    @DisplayName("Should maintain consistent behavior across multiple validation calls")
    fun shouldMaintainConsistentBehaviorAcrossMultipleValidationCalls() {
        // Given
        val token = "consistent.test.token"
        val username = "consistentuser"

        every { jwtTokenService.validateAccessTokenAndGetUsername(token) } returns username

        // When
        val result1 = jwtTokenServiceAdapter.validateAccessTokenAndGetUsername(token)
        val result2 = jwtTokenServiceAdapter.validateAccessTokenAndGetUsername(token)

        // Then
        assertEquals(username, result1)
        assertEquals(username, result2)
        assertEquals(result1, result2)
        verify(exactly = 2) { jwtTokenService.validateAccessTokenAndGetUsername(token) }
    }
}
