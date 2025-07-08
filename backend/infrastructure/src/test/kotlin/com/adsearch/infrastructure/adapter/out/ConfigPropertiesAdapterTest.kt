package com.adsearch.infrastructure.adapter.out

import com.adsearch.infrastructure.service.ConfigService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DisplayName("Config Properties Adapter Tests")
class ConfigPropertiesAdapterTest {

    private lateinit var configPropertiesAdapter: ConfigPropertiesAdapter
    private val configService = mockk<ConfigService>()

    @BeforeEach
    fun setUp() {
        configPropertiesAdapter = ConfigPropertiesAdapter(configService)
    }

    @Test
    @DisplayName("Should return password reset token expiration when requested")
    fun shouldReturnPasswordResetTokenExpirationWhenRequested() {
        // Given
        val expectedExpiration = 3600L // 1 hour in seconds

        every { configService.getPasswordResetTokenExpiration() } returns expectedExpiration

        // When
        val actualExpiration = configPropertiesAdapter.getPasswordResetTokenExpiration()

        // Then
        assertEquals(expectedExpiration, actualExpiration)
        verify(exactly = 1) { configService.getPasswordResetTokenExpiration() }
    }

    @Test
    @DisplayName("Should return refresh token expiration when requested")
    fun shouldReturnRefreshTokenExpirationWhenRequested() {
        // Given
        val expectedExpiration = 604800L // 7 days in seconds

        every { configService.getRefreshTokenExpiration() } returns expectedExpiration

        // When
        val actualExpiration = configPropertiesAdapter.getRefreshTokenExpiration()

        // Then
        assertEquals(expectedExpiration, actualExpiration)
        verify(exactly = 1) { configService.getRefreshTokenExpiration() }
    }

    @Test
    @DisplayName("Should delegate password reset token expiration call to config service")
    fun shouldDelegatePasswordResetTokenExpirationCallToConfigService() {
        // Given
        val delegatedExpiration = 1800L // 30 minutes in seconds

        every { configService.getPasswordResetTokenExpiration() } returns delegatedExpiration

        // When
        val result = configPropertiesAdapter.getPasswordResetTokenExpiration()

        // Then
        assertEquals(delegatedExpiration, result)
        verify(exactly = 1) { configService.getPasswordResetTokenExpiration() }
    }

    @Test
    @DisplayName("Should delegate refresh token expiration call to config service")
    fun shouldDelegateRefreshTokenExpirationCallToConfigService() {
        // Given
        val delegatedExpiration = 86400L // 1 day in seconds

        every { configService.getRefreshTokenExpiration() } returns delegatedExpiration

        // When
        val result = configPropertiesAdapter.getRefreshTokenExpiration()

        // Then
        assertEquals(delegatedExpiration, result)
        verify(exactly = 1) { configService.getRefreshTokenExpiration() }
    }

    @Test
    @DisplayName("Should handle zero expiration values correctly")
    fun shouldHandleZeroExpirationValuesCorrectly() {
        // Given
        val zeroExpiration = 0L

        every { configService.getPasswordResetTokenExpiration() } returns zeroExpiration
        every { configService.getRefreshTokenExpiration() } returns zeroExpiration

        // When
        val passwordResetExpiration = configPropertiesAdapter.getPasswordResetTokenExpiration()
        val refreshTokenExpiration = configPropertiesAdapter.getRefreshTokenExpiration()

        // Then
        assertEquals(zeroExpiration, passwordResetExpiration)
        assertEquals(zeroExpiration, refreshTokenExpiration)
        verify(exactly = 1) { configService.getPasswordResetTokenExpiration() }
        verify(exactly = 1) { configService.getRefreshTokenExpiration() }
    }

    @Test
    @DisplayName("Should handle large expiration values correctly")
    fun shouldHandleLargeExpirationValuesCorrectly() {
        // Given
        val largePasswordResetExpiration = Long.MAX_VALUE
        val largeRefreshTokenExpiration = Long.MAX_VALUE - 1

        every { configService.getPasswordResetTokenExpiration() } returns largePasswordResetExpiration
        every { configService.getRefreshTokenExpiration() } returns largeRefreshTokenExpiration

        // When
        val passwordResetExpiration = configPropertiesAdapter.getPasswordResetTokenExpiration()
        val refreshTokenExpiration = configPropertiesAdapter.getRefreshTokenExpiration()

        // Then
        assertEquals(largePasswordResetExpiration, passwordResetExpiration)
        assertEquals(largeRefreshTokenExpiration, refreshTokenExpiration)
        verify(exactly = 1) { configService.getPasswordResetTokenExpiration() }
        verify(exactly = 1) { configService.getRefreshTokenExpiration() }
    }

    @Test
    @DisplayName("Should maintain consistent behavior across multiple calls")
    fun shouldMaintainConsistentBehaviorAcrossMultipleCalls() {
        // Given
        val passwordResetExpiration = 7200L // 2 hours
        val refreshTokenExpiration = 1209600L // 14 days

        every { configService.getPasswordResetTokenExpiration() } returns passwordResetExpiration
        every { configService.getRefreshTokenExpiration() } returns refreshTokenExpiration

        // When
        val firstPasswordResetCall = configPropertiesAdapter.getPasswordResetTokenExpiration()
        val secondPasswordResetCall = configPropertiesAdapter.getPasswordResetTokenExpiration()
        val firstRefreshTokenCall = configPropertiesAdapter.getRefreshTokenExpiration()
        val secondRefreshTokenCall = configPropertiesAdapter.getRefreshTokenExpiration()

        // Then
        assertEquals(passwordResetExpiration, firstPasswordResetCall)
        assertEquals(passwordResetExpiration, secondPasswordResetCall)
        assertEquals(refreshTokenExpiration, firstRefreshTokenCall)
        assertEquals(refreshTokenExpiration, secondRefreshTokenCall)
        assertEquals(firstPasswordResetCall, secondPasswordResetCall)
        assertEquals(firstRefreshTokenCall, secondRefreshTokenCall)
        verify(exactly = 2) { configService.getPasswordResetTokenExpiration() }
        verify(exactly = 2) { configService.getRefreshTokenExpiration() }
    }

    @Test
    @DisplayName("Should handle different expiration values for different tokens")
    fun shouldHandleDifferentExpirationValuesForDifferentTokens() {
        // Given
        val passwordResetExpiration = 900L // 15 minutes
        val refreshTokenExpiration = 2592000L // 30 days

        every { configService.getPasswordResetTokenExpiration() } returns passwordResetExpiration
        every { configService.getRefreshTokenExpiration() } returns refreshTokenExpiration

        // When
        val passwordResetResult = configPropertiesAdapter.getPasswordResetTokenExpiration()
        val refreshTokenResult = configPropertiesAdapter.getRefreshTokenExpiration()

        // Then
        assertEquals(passwordResetExpiration, passwordResetResult)
        assertEquals(refreshTokenExpiration, refreshTokenResult)
        assertTrue(passwordResetResult != refreshTokenResult)
        verify(exactly = 1) { configService.getPasswordResetTokenExpiration() }
        verify(exactly = 1) { configService.getRefreshTokenExpiration() }
    }
}
