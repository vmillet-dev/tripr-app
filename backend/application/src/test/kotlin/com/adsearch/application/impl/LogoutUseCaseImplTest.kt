package com.adsearch.application.impl

import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("LogoutUseCaseImpl Tests")
class LogoutUseCaseImplTest {

    private val refreshTokenPersistence: RefreshTokenPersistencePort = mockk()

    private lateinit var logoutUseCase: LogoutUseCaseImpl

    @BeforeEach
    fun setUp() {
        logoutUseCase = LogoutUseCaseImpl(refreshTokenPersistence)
    }

    @Test
    @DisplayName("Should successfully logout user with valid refresh token")
    fun shouldSuccessfullyLogoutUserWithValidRefreshToken() {
        // Given
        val refreshToken = "valid-refresh-token"

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When
        logoutUseCase.logout(refreshToken)

        // Then
        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when logout attempted with null token")
    fun shouldThrowInvalidTokenExceptionWhenLogoutAttemptedWithNullToken() {
        // Given
        val refreshToken: String? = null

        // When & Then
        val exception = assertThrows<InvalidTokenException> {
            logoutUseCase.logout(refreshToken)
        }

        assertEquals("Logout attempted without refresh token", exception.message)
        
        verify(exactly = 0) { refreshTokenPersistence.deleteByToken(any()) }
    }

    @Test
    @DisplayName("Should delete refresh token when logout is successful")
    fun shouldDeleteRefreshTokenWhenLogoutIsSuccessful() {
        // Given
        val refreshToken = "valid-refresh-token"

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When
        logoutUseCase.logout(refreshToken)

        // Then
        verify(exactly = 1) { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should handle empty string token as valid input")
    fun shouldHandleEmptyStringTokenAsValidInput() {
        // Given
        val refreshToken = ""

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When
        logoutUseCase.logout(refreshToken)

        // Then
        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should handle whitespace-only token as valid input")
    fun shouldHandleWhitespaceOnlyTokenAsValidInput() {
        // Given
        val refreshToken = "   "

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When
        logoutUseCase.logout(refreshToken)

        // Then
        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should not perform any additional validation on token format")
    fun shouldNotPerformAnyAdditionalValidationOnTokenFormat() {
        // Given
        val refreshToken = "any-random-string-123!@#"

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When
        logoutUseCase.logout(refreshToken)

        // Then
        verify { refreshTokenPersistence.deleteByToken(refreshToken) }
    }

    @Test
    @DisplayName("Should delegate token deletion to persistence layer")
    fun shouldDelegateTokenDeletionToPersistenceLayer() {
        // Given
        val refreshToken = "test-token"

        every { refreshTokenPersistence.deleteByToken(refreshToken) } returns Unit

        // When
        logoutUseCase.logout(refreshToken)

        // Then
        verify(exactly = 1) { refreshTokenPersistence.deleteByToken(refreshToken) }
    }
}
