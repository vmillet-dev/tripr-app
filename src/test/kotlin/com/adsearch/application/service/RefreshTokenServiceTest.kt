package com.adsearch.application.service

import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.RefreshTokenRepositoryPort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.UUID

class RefreshTokenServiceTest {
    
    private lateinit var refreshTokenService: RefreshTokenService
    private lateinit var refreshTokenRepository: RefreshTokenRepositoryPort
    private val refreshTokenExpiration = 604800000L // 7 days
    
    @BeforeEach
    fun setUp() {
        refreshTokenRepository = mockk()
        
        // Mock common repository operations
        coEvery { refreshTokenRepository.deleteByUserId(any()) } returns Unit
        
        refreshTokenService = RefreshTokenService(refreshTokenRepository, refreshTokenExpiration)
    }
    
    @Test
    fun `should create refresh token`() = runBlocking {
        // Given
        val user = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "password"
        )
        
        coEvery { refreshTokenRepository.save(any()) } answers { firstArg() }
        
        // When
        val refreshToken = refreshTokenService.createRefreshToken(user)
        
        // Then
        assertNotNull(refreshToken)
        assertEquals(user.id, refreshToken.userId)
        assertNotNull(refreshToken.token)
        assertNotNull(refreshToken.expiryDate)
        
        // Verify expiry date is set correctly
        val expectedExpiryTime = Instant.now().plusMillis(refreshTokenExpiration)
        assertTrue(refreshToken.expiryDate.isAfter(Instant.now()))
        assertTrue(refreshToken.expiryDate.isBefore(expectedExpiryTime.plusSeconds(10))) // Allow small timing difference
        
        coVerify { refreshTokenRepository.save(any()) }
    }
    
    @Test
    fun `should verify valid token`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val validToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = "valid-token",
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration),
            revoked = false
        )
        
        // When
        val result = refreshTokenService.verifyExpiration(validToken)
        
        // Then
        assertEquals(validToken, result)
    }
    
    @Test
    fun `should throw exception for expired token`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val expiredToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = "expired-token",
            expiryDate = Instant.now().minusSeconds(10),
            revoked = false
        )
        
        coEvery { refreshTokenRepository.deleteById(any()) } returns Unit
        
        // When/Then
        assertThrows<TokenExpiredException> {
            runBlocking {
                refreshTokenService.verifyExpiration(expiredToken)
            }
        }
        
        coVerify { refreshTokenRepository.deleteById(expiredToken.id) }
    }
    
    @Test
    fun `should throw exception for revoked token`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        val revokedToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = userId,
            token = "revoked-token",
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration),
            revoked = true
        )
        
        coEvery { refreshTokenRepository.deleteById(any()) } returns Unit
        
        // When/Then
        assertThrows<TokenExpiredException> {
            runBlocking {
                refreshTokenService.verifyExpiration(revokedToken)
            }
        }
        
        coVerify { refreshTokenRepository.deleteById(revokedToken.id) }
    }
    
    @Test
    fun `should find token by token string`() = runBlocking {
        // Given
        val tokenString = "test-token"
        val refreshToken = RefreshToken(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            token = tokenString,
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration)
        )
        
        coEvery { refreshTokenRepository.findByToken(tokenString) } returns refreshToken
        
        // When
        val result = refreshTokenService.findByToken(tokenString)
        
        // Then
        assertEquals(refreshToken, result)
        coVerify { refreshTokenRepository.findByToken(tokenString) }
    }
    
    @Test
    fun `should delete tokens by user ID`() = runBlocking {
        // Given
        val userId = UUID.randomUUID()
        
        coEvery { refreshTokenRepository.deleteByUserId(userId) } returns Unit
        
        // When
        refreshTokenService.deleteByUserId(userId)
        
        // Then
        coVerify { refreshTokenRepository.deleteByUserId(userId) }
    }
}
