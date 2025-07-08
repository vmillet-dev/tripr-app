package com.adsearch.application.impl

import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import com.adsearch.domain.port.out.UserPersistencePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("RefreshTokenUseCaseImpl Tests")
class RefreshTokenUseCaseImplTest {

    private val refreshTokenPersistence: RefreshTokenPersistencePort = mockk()
    private val userPersistence: UserPersistencePort = mockk()
    private val jwtTokenService: JwtTokenServicePort = mockk()

    private lateinit var refreshTokenUseCase: RefreshTokenUseCaseImpl

    @BeforeEach
    fun setUp() {
        refreshTokenUseCase = RefreshTokenUseCaseImpl(
            refreshTokenPersistence,
            userPersistence,
            jwtTokenService
        )
    }

    @Test
    @DisplayName("Should successfully refresh access token with valid refresh token")
    fun shouldSuccessfullyRefreshAccessTokenWithValidRefreshToken() {
        // Given
        val refreshToken = "valid-refresh-token"
        val userId = 1L
        val user = UserDom(userId, "testuser", "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val refreshTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().plusSeconds(3600), false)
        val newAccessToken = "new-access-token"

        every { refreshTokenPersistence.findByToken(refreshToken) } returns refreshTokenDom
        every { userPersistence.findById(userId) } returns user
        every { jwtTokenService.createAccessToken(user) } returns newAccessToken

        // When
        val result = refreshTokenUseCase.refreshAccessToken(refreshToken)

        // Then
        assertNotNull(result)
        assertEquals(newAccessToken, result.accessToken)
        assertNull(result.refreshToken)
        
        verify { refreshTokenPersistence.findByToken(refreshToken) }
        verify { userPersistence.findById(userId) }
        verify { jwtTokenService.createAccessToken(user) }
        verify(exactly = 0) { refreshTokenPersistence.deleteByToken(any()) }
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is null")
    fun shouldThrowInvalidTokenExceptionWhenRefreshTokenIsNull() {
        // Given
        val refreshToken: String? = null

        // When & Then
        val exception = assertThrows<InvalidTokenException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        assertEquals("Token refresh failed - refresh token missing", exception.message)
        
        verify(exactly = 0) { refreshTokenPersistence.findByToken(any()) }
        verify(exactly = 0) { userPersistence.findById(any()) }
        verify(exactly = 0) { jwtTokenService.createAccessToken(any()) }
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is not found")
    fun shouldThrowInvalidTokenExceptionWhenRefreshTokenIsNotFound() {
        // Given
        val refreshToken = "invalid-refresh-token"

        every { refreshTokenPersistence.findByToken(refreshToken) } returns null

        // When & Then
        val exception = assertThrows<InvalidTokenException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        assertEquals("Token refresh failed - invalid refresh token provided", exception.message)
        
        verify { refreshTokenPersistence.findByToken(refreshToken) }
        verify(exactly = 0) { userPersistence.findById(any()) }
        verify(exactly = 0) { jwtTokenService.createAccessToken(any()) }
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when refresh token is expired")
    fun shouldThrowTokenExpiredExceptionWhenRefreshTokenIsExpired() {
        // Given
        val refreshToken = "expired-refresh-token"
        val userId = 1L
        val expiredTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().minusSeconds(3600), false)

        every { refreshTokenPersistence.findByToken(refreshToken) } returns expiredTokenDom
        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When & Then
        val exception = assertThrows<TokenExpiredException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        assertEquals("Token refresh failed - refresh token expired or revoked for user id: $userId", exception.message)
        
        verify { refreshTokenPersistence.findByToken(refreshToken) }
        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
        verify(exactly = 0) { userPersistence.findById(any()) }
        verify(exactly = 0) { jwtTokenService.createAccessToken(any()) }
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when refresh token is revoked")
    fun shouldThrowTokenExpiredExceptionWhenRefreshTokenIsRevoked() {
        // Given
        val refreshToken = "revoked-refresh-token"
        val userId = 1L
        val revokedTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().plusSeconds(3600), true)

        every { refreshTokenPersistence.findByToken(refreshToken) } returns revokedTokenDom
        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When & Then
        val exception = assertThrows<TokenExpiredException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        assertEquals("Token refresh failed - refresh token expired or revoked for user id: $userId", exception.message)
        
        verify { refreshTokenPersistence.findByToken(refreshToken) }
        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
        verify(exactly = 0) { userPersistence.findById(any()) }
        verify(exactly = 0) { jwtTokenService.createAccessToken(any()) }
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    fun shouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
        // Given
        val refreshToken = "valid-refresh-token"
        val userId = 1L
        val refreshTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().plusSeconds(3600), false)

        every { refreshTokenPersistence.findByToken(refreshToken) } returns refreshTokenDom
        every { userPersistence.findById(userId) } returns null

        // When & Then
        val exception = assertThrows<UserNotFoundException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        assertEquals("Token refresh failed - user not found with user id: $userId", exception.message)
        
        verify { refreshTokenPersistence.findByToken(refreshToken) }
        verify { userPersistence.findById(userId) }
        verify(exactly = 0) { jwtTokenService.createAccessToken(any()) }
        verify(exactly = 0) { refreshTokenPersistence.deleteByToken(any()) }
    }

    @Test
    @DisplayName("Should delete expired token when token is expired")
    fun shouldDeleteExpiredTokenWhenTokenIsExpired() {
        // Given
        val refreshToken = "expired-refresh-token"
        val userId = 1L
        val expiredTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().minusSeconds(3600), false)

        every { refreshTokenPersistence.findByToken(refreshToken) } returns expiredTokenDom
        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When & Then
        assertThrows<TokenExpiredException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should delete revoked token when token is revoked")
    fun shouldDeleteRevokedTokenWhenTokenIsRevoked() {
        // Given
        val refreshToken = "revoked-refresh-token"
        val userId = 1L
        val revokedTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().plusSeconds(3600), true)

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit
        every { refreshTokenPersistence.findByToken(refreshToken) } returns revokedTokenDom

        // When & Then
        assertThrows<TokenExpiredException> {
            refreshTokenUseCase.refreshAccessToken(refreshToken)
        }

        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should return AuthResponse with only access token on successful refresh")
    fun shouldReturnAuthResponseWithOnlyAccessTokenOnSuccessfulRefresh() {
        // Given
        val refreshToken = "valid-refresh-token"
        val userId = 1L
        val user = UserDom(userId, "testuser", "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val refreshTokenDom = RefreshTokenDom(userId, refreshToken, Instant.now().plusSeconds(3600), false)
        val newAccessToken = "new-access-token"

        every { refreshTokenPersistence.findByToken(refreshToken) } returns refreshTokenDom
        every { userPersistence.findById(userId) } returns user
        every { jwtTokenService.createAccessToken(user) } returns newAccessToken

        // When
        val result = refreshTokenUseCase.refreshAccessToken(refreshToken)

        // Then
        assertEquals(newAccessToken, result.accessToken)
        assertNull(result.refreshToken)
    }
}
