package com.adsearch.infrastructure.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import kotlin.test.assertEquals

@DisplayName("Config Service Tests")
class ConfigServiceTest {

    private lateinit var configService: ConfigService
    private val testRefreshTokenExpiration = 604800L // 7 days in seconds
    private val testPasswordResetTokenExpiration = 3600L // 1 hour in seconds

    @BeforeEach
    fun setUp() {
        configService = ConfigService(testRefreshTokenExpiration, testPasswordResetTokenExpiration)
    }

    @Test
    @DisplayName("Should return correct refresh token expiration when requested")
    fun shouldReturnCorrectRefreshTokenExpirationWhenRequested() {
        // Given
        // ConfigService is initialized with test values in setUp()

        // When
        val refreshTokenExpiration = configService.getRefreshTokenExpiration()

        // Then
        assertEquals(testRefreshTokenExpiration, refreshTokenExpiration)
    }

    @Test
    @DisplayName("Should return correct password reset token expiration when requested")
    fun shouldReturnCorrectPasswordResetTokenExpirationWhenRequested() {
        // Given
        // ConfigService is initialized with test values in setUp()

        // When
        val passwordResetTokenExpiration = configService.getPasswordResetTokenExpiration()

        // Then
        assertEquals(testPasswordResetTokenExpiration, passwordResetTokenExpiration)
    }

    @Test
    @DisplayName("Should maintain consistent values across multiple calls")
    fun shouldMaintainConsistentValuesAcrossMultipleCalls() {
        // Given
        // ConfigService is initialized with test values in setUp()

        // When
        val firstRefreshCall = configService.getRefreshTokenExpiration()
        val secondRefreshCall = configService.getRefreshTokenExpiration()
        val firstPasswordResetCall = configService.getPasswordResetTokenExpiration()
        val secondPasswordResetCall = configService.getPasswordResetTokenExpiration()

        // Then
        assertEquals(firstRefreshCall, secondRefreshCall)
        assertEquals(firstPasswordResetCall, secondPasswordResetCall)
        assertEquals(testRefreshTokenExpiration, firstRefreshCall)
        assertEquals(testPasswordResetTokenExpiration, firstPasswordResetCall)
    }

    @Test
    @DisplayName("Should handle different configuration values correctly")
    fun shouldHandleDifferentConfigurationValuesCorrectly() {
        // Given
        val customRefreshExpiration = 86400L // 1 day
        val customPasswordResetExpiration = 1800L // 30 minutes
        val customConfigService = ConfigService(customRefreshExpiration, customPasswordResetExpiration)

        // When
        val refreshExpiration = customConfigService.getRefreshTokenExpiration()
        val passwordResetExpiration = customConfigService.getPasswordResetTokenExpiration()

        // Then
        assertEquals(customRefreshExpiration, refreshExpiration)
        assertEquals(customPasswordResetExpiration, passwordResetExpiration)
    }

    @Test
    @DisplayName("Should handle zero values correctly")
    fun shouldHandleZeroValuesCorrectly() {
        // Given
        val zeroConfigService = ConfigService(0L, 0L)

        // When
        val refreshExpiration = zeroConfigService.getRefreshTokenExpiration()
        val passwordResetExpiration = zeroConfigService.getPasswordResetTokenExpiration()

        // Then
        assertEquals(0L, refreshExpiration)
        assertEquals(0L, passwordResetExpiration)
    }

    @Test
    @DisplayName("Should handle large values correctly")
    fun shouldHandleLargeValuesCorrectly() {
        // Given
        val largeRefreshExpiration = Long.MAX_VALUE
        val largePasswordResetExpiration = Long.MAX_VALUE - 1
        val largeConfigService = ConfigService(largeRefreshExpiration, largePasswordResetExpiration)

        // When
        val refreshExpiration = largeConfigService.getRefreshTokenExpiration()
        val passwordResetExpiration = largeConfigService.getPasswordResetTokenExpiration()

        // Then
        assertEquals(largeRefreshExpiration, refreshExpiration)
        assertEquals(largePasswordResetExpiration, passwordResetExpiration)
    }
}
