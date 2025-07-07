package com.adsearch.application.impl

import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.out.ConfigPropertiesPort
import com.adsearch.domain.port.out.EmailServicePort
import com.adsearch.domain.port.out.PasswordResetTokenPersistencePort
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DisplayName("PasswordResetUseCaseImpl Tests")
class PasswordResetUseCaseImplTest {

    private val configProperties: ConfigPropertiesPort = mockk()
    private val userPersistence: UserPersistencePort = mockk()
    private val passwordResetTokenPersistence: PasswordResetTokenPersistencePort = mockk()
    private val emailService: EmailServicePort = mockk()
    private val authenticationService: AuthenticationServicePort = mockk()

    private lateinit var passwordResetUseCase: PasswordResetUseCaseImpl

    @BeforeEach
    fun setUp() {
        passwordResetUseCase = PasswordResetUseCaseImpl(
            configProperties,
            userPersistence,
            passwordResetTokenPersistence,
            emailService,
            authenticationService
        )
    }

    // requestPasswordReset tests
    @Test
    @DisplayName("Should successfully request password reset for existing user")
    fun shouldSuccessfullyRequestPasswordResetForExistingUser() {
        // Given
        val username = "testuser"
        val user = UserDom(1L, username, "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val tokenExpiration = 3600L
        val capturedTokens = mutableListOf<PasswordResetTokenDom>()

        every { userPersistence.findByUsername(username) } returns user
        every { passwordResetTokenPersistence.deleteByUserId(user.id) } returns Unit
        every { configProperties.getPasswordResetTokenExpiration() } returns tokenExpiration
        every { passwordResetTokenPersistence.save(capture(capturedTokens)) } returns Unit
        every { emailService.sendPasswordResetEmail(user.email, any()) } returns Unit

        // When
        passwordResetUseCase.requestPasswordReset(username)

        // Then
        val savedToken = capturedTokens.first()
        assertEquals(user.id, savedToken.userId)
        assertTrue(savedToken.expiryDate.isAfter(Instant.now().plusSeconds(tokenExpiration - 10)))
        assertTrue(savedToken.expiryDate.isBefore(Instant.now().plusSeconds(tokenExpiration + 10)))
        
        verify { userPersistence.findByUsername(username) }
        verify { passwordResetTokenPersistence.deleteByUserId(user.id) }
        verify { configProperties.getPasswordResetTokenExpiration() }
        verify { passwordResetTokenPersistence.save(any<PasswordResetTokenDom>()) }
        verify { emailService.sendPasswordResetEmail(user.email, savedToken.token) }
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when requesting password reset for non-existing user")
    fun shouldThrowUserNotFoundExceptionWhenRequestingPasswordResetForNonExistingUser() {
        // Given
        val username = "nonexistentuser"

        every { userPersistence.findByUsername(username) } returns null

        // When & Then
        val exception = assertThrows<UserNotFoundException> {
            passwordResetUseCase.requestPasswordReset(username)
        }

        assertEquals("Password reset request failed - user not found with username: $username", exception.message)
        
        verify { userPersistence.findByUsername(username) }
        verify(exactly = 0) { passwordResetTokenPersistence.deleteByUserId(any()) }
        verify(exactly = 0) { passwordResetTokenPersistence.save(any<PasswordResetTokenDom>()) }
        verify(exactly = 0) { emailService.sendPasswordResetEmail(any(), any()) }
    }

    @Test
    @DisplayName("Should delete existing tokens before creating new one when requesting password reset")
    fun shouldDeleteExistingTokensBeforeCreatingNewOneWhenRequestingPasswordReset() {
        // Given
        val username = "testuser"
        val user = UserDom(1L, username, "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val tokenExpiration = 3600L

        every { userPersistence.findByUsername(username) } returns user
        every { passwordResetTokenPersistence.deleteByUserId(user.id) } returns Unit
        every { configProperties.getPasswordResetTokenExpiration() } returns tokenExpiration
        every { passwordResetTokenPersistence.save(any<PasswordResetTokenDom>()) } returns Unit
        every { emailService.sendPasswordResetEmail(user.email, any()) } returns Unit

        // When
        passwordResetUseCase.requestPasswordReset(username)

        // Then
        verify { passwordResetTokenPersistence.deleteByUserId(user.id) }
        verify { passwordResetTokenPersistence.save(any<PasswordResetTokenDom>()) }
    }

    // resetPassword tests
    @Test
    @DisplayName("Should successfully reset password with valid token")
    fun shouldSuccessfullyResetPasswordWithValidToken() {
        // Given
        val token = "valid-reset-token"
        val newPassword = "newPassword123"
        val hashedNewPassword = "hashedNewPassword123"
        val userId = 1L
        val user = UserDom(userId, "testuser", "test@example.com", "oldHashedPassword", setOf("ROLE_USER"), true)
        val resetToken = PasswordResetTokenDom(userId, token, Instant.now().plusSeconds(3600))
        val updatedUser = user.changePassword(hashedNewPassword)

        every { passwordResetTokenPersistence.findByToken(token) } returns resetToken
        every { userPersistence.findById(userId) } returns user
        every { authenticationService.generateHashedPassword(newPassword) } returns hashedNewPassword
        every { userPersistence.save(updatedUser) } returns Unit
        every { passwordResetTokenPersistence.deleteByUserId(userId) } returns Unit

        // When
        passwordResetUseCase.resetPassword(token, newPassword)

        // Then
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify { userPersistence.findById(userId) }
        verify { authenticationService.generateHashedPassword(newPassword) }
        verify { userPersistence.save(updatedUser) }
        verify { passwordResetTokenPersistence.deleteByUserId(userId) }
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when resetting password with invalid token")
    fun shouldThrowInvalidTokenExceptionWhenResettingPasswordWithInvalidToken() {
        // Given
        val token = "invalid-reset-token"
        val newPassword = "newPassword123"

        every { passwordResetTokenPersistence.findByToken(token) } returns null

        // When & Then
        val exception = assertThrows<InvalidTokenException> {
            passwordResetUseCase.resetPassword(token, newPassword)
        }

        assertEquals("Password reset failed - token not found", exception.message)
        
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify(exactly = 0) { userPersistence.findById(any()) }
        verify(exactly = 0) { authenticationService.generateHashedPassword(any()) }
        verify(exactly = 0) { userPersistence.save(any<UserDom>()) }
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when resetting password with expired token")
    fun shouldThrowTokenExpiredExceptionWhenResettingPasswordWithExpiredToken() {
        // Given
        val token = "expired-reset-token"
        val newPassword = "newPassword123"
        val userId = 1L
        val expiredToken = PasswordResetTokenDom(userId, token, Instant.now().minusSeconds(3600))

        every { passwordResetTokenPersistence.findByToken(token) } returns expiredToken
        every { passwordResetTokenPersistence.deleteByToken(token) } returns Unit

        // When & Then
        val exception = assertThrows<TokenExpiredException> {
            passwordResetUseCase.resetPassword(token, newPassword)
        }

        assertEquals("Password reset failed - expired token for user id: $userId", exception.message)
        
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify { passwordResetTokenPersistence.deleteByToken(token) }
        verify(exactly = 0) { userPersistence.findById(any()) }
        verify(exactly = 0) { authenticationService.generateHashedPassword(any()) }
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when resetting password for non-existing user")
    fun shouldThrowUserNotFoundExceptionWhenResettingPasswordForNonExistingUser() {
        // Given
        val token = "valid-reset-token"
        val newPassword = "newPassword123"
        val userId = 1L
        val resetToken = PasswordResetTokenDom(userId, token, Instant.now().plusSeconds(3600))

        every { passwordResetTokenPersistence.findByToken(token) } returns resetToken
        every { userPersistence.findById(userId) } returns null

        // When & Then
        val exception = assertThrows<UserNotFoundException> {
            passwordResetUseCase.resetPassword(token, newPassword)
        }

        assertEquals("Password reset failed - user not found with user id: $userId", exception.message)
        
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify { userPersistence.findById(userId) }
        verify(exactly = 0) { authenticationService.generateHashedPassword(any()) }
        verify(exactly = 0) { userPersistence.save(any<UserDom>()) }
    }

    @Test
    @DisplayName("Should delete all tokens after successful password reset")
    fun shouldDeleteAllTokensAfterSuccessfulPasswordReset() {
        // Given
        val token = "valid-reset-token"
        val newPassword = "newPassword123"
        val hashedNewPassword = "hashedNewPassword123"
        val userId = 1L
        val user = UserDom(userId, "testuser", "test@example.com", "oldHashedPassword", setOf("ROLE_USER"), true)
        val resetToken = PasswordResetTokenDom(userId, token, Instant.now().plusSeconds(3600))
        val updatedUser = user.changePassword(hashedNewPassword)

        every { passwordResetTokenPersistence.findByToken(token) } returns resetToken
        every { userPersistence.findById(userId) } returns user
        every { authenticationService.generateHashedPassword(newPassword) } returns hashedNewPassword
        every { userPersistence.save(updatedUser) } returns Unit
        every { passwordResetTokenPersistence.deleteByUserId(userId) } returns Unit

        // When
        passwordResetUseCase.resetPassword(token, newPassword)

        // Then
        verify { passwordResetTokenPersistence.deleteByUserId(userId) }
    }

    // validateToken tests
    @Test
    @DisplayName("Should return true when validating valid token")
    fun shouldReturnTrueWhenValidatingValidToken() {
        // Given
        val token = "valid-reset-token"
        val userId = 1L
        val validToken = PasswordResetTokenDom(userId, token, Instant.now().plusSeconds(3600))

        every { passwordResetTokenPersistence.findByToken(token) } returns validToken

        // When
        val result = passwordResetUseCase.validateToken(token)

        // Then
        assertTrue(result)
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify(exactly = 0) { passwordResetTokenPersistence.deleteByToken(any()) }
    }

    @Test
    @DisplayName("Should return false when validating non-existing token")
    fun shouldReturnFalseWhenValidatingNonExistingToken() {
        // Given
        val token = "non-existing-token"

        every { passwordResetTokenPersistence.findByToken(token) } returns null

        // When
        val result = passwordResetUseCase.validateToken(token)

        // Then
        assertFalse(result)
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify(exactly = 0) { passwordResetTokenPersistence.deleteByToken(any()) }
    }

    @Test
    @DisplayName("Should return false and delete token when validating expired token")
    fun shouldReturnFalseAndDeleteTokenWhenValidatingExpiredToken() {
        // Given
        val token = "expired-reset-token"
        val userId = 1L
        val expiredToken = PasswordResetTokenDom(userId, token, Instant.now().minusSeconds(3600))

        every { passwordResetTokenPersistence.findByToken(token) } returns expiredToken
        every { passwordResetTokenPersistence.deleteByToken(token) } returns Unit

        // When
        val result = passwordResetUseCase.validateToken(token)

        // Then
        assertFalse(result)
        verify { passwordResetTokenPersistence.findByToken(token) }
        verify { passwordResetTokenPersistence.deleteByToken(token) }
    }

    @Test
    @DisplayName("Should clean up expired token during validation")
    fun shouldCleanUpExpiredTokenDuringValidation() {
        // Given
        val token = "expired-reset-token"
        val userId = 1L
        val expiredToken = PasswordResetTokenDom(userId, token, Instant.now().minusSeconds(3600))

        every { passwordResetTokenPersistence.findByToken(token) } returns expiredToken
        every { passwordResetTokenPersistence.deleteByToken(token) } returns Unit

        // When
        passwordResetUseCase.validateToken(token)

        // Then
        verify { passwordResetTokenPersistence.deleteByToken(token) }
    }
}
