package com.adsearch.application.service

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.common.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.EmailServicePort
import com.adsearch.domain.port.PasswordResetTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

class PasswordResetServiceTest {

    private lateinit var passwordResetService: PasswordResetService
    private lateinit var userRepository: UserPersistencePort
    private lateinit var tokenRepository: PasswordResetTokenPersistencePort
    private lateinit var emailService: EmailServicePort
    private lateinit var passwordEncoder: PasswordEncoder

    private val tokenExpiration = 86400000L // 24 hours
    private val baseUrl = "http://localhost:8080/reset-password"

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        tokenRepository = mockk()
        emailService = mockk()
        passwordEncoder = mockk()

        passwordResetService = PasswordResetService(
            userRepository = userRepository,
            tokenRepository = tokenRepository,
            emailService = emailService,
            passwordEncoder = passwordEncoder,
            tokenExpiration = tokenExpiration,
            baseUrl = baseUrl
        )
    }

    @Test
    fun `should request password reset successfully`() = runBlocking {
        // Given
        val username = "user"
        val user = User(
            id = 1L,
            username = username,
            password = "encoded-password"
        )

        coEvery { userRepository.findByUsername(username) } returns user
        coJustRun { tokenRepository.deleteByUserId(user.id) }
        coEvery { tokenRepository.save(any()) } answers { firstArg() }
        coJustRun { emailService.sendPasswordResetEmail(any(), any()) }

        // When
        passwordResetService.requestPasswordReset(username)

        // Then
        coVerify { userRepository.findByUsername(username) }
        coVerify { tokenRepository.deleteByUserId(user.id) }
        coVerify { tokenRepository.save(any()) }
        coVerify { emailService.sendPasswordResetEmail(username, match { it.contains(baseUrl) }) }
    }

    @Test
    fun `should throw UserNotFoundException when requesting reset for non-existent user`() = runBlocking {
        // Given
        val username = "non-existent-user"

        coEvery { userRepository.findByUsername(username) } returns null

        // When/Then
        assertThrows<UserNotFoundException> {
            passwordResetService.requestPasswordReset(username)
        }

        coVerify { userRepository.findByUsername(username) }
    }

    @Test
    fun `should reset password successfully`() = runBlocking {
        // Given
        val token = "valid-token"
        val newPassword = "new-password"
        val encodedPassword = "encoded-new-password"

        val userId = 1L
        val user = User(
            id = userId,
            username = "user",
            password = "old-encoded-password"
        )

        val resetToken = PasswordResetToken(
            userId = userId,
            token = token,
            expiryDate = Instant.now().plusMillis(tokenExpiration),
            used = false
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken
        coEvery { userRepository.findById(userId) } returns user
        coEvery { passwordEncoder.encode(newPassword) } returns encodedPassword
        coEvery { userRepository.save(any()) } answers { firstArg() }
        coEvery { tokenRepository.save(any()) } answers { firstArg() }
        coJustRun { tokenRepository.deleteByUserId(userId) }

        // When
        passwordResetService.resetPassword(token, newPassword)

        // Then
        coVerify { tokenRepository.findByToken(token) }
        coVerify { userRepository.findById(userId) }
        coVerify { passwordEncoder.encode(newPassword) }
        coVerify { userRepository.save(match { it.password == encodedPassword }) }
        coVerify { tokenRepository.save(match { it.used }) }
        coVerify { tokenRepository.deleteByUserId(userId) }
    }

    @Test
    fun `should throw InvalidTokenException when token is invalid`() = runBlocking {
        // Given
        val token = "invalid-token"
        val newPassword = "new-password"

        coEvery { tokenRepository.findByToken(token) } returns null

        // When/Then
        assertThrows<InvalidTokenException> {
            passwordResetService.resetPassword(token, newPassword)
        }

        coVerify { tokenRepository.findByToken(token) }
    }

    @Test
    fun `should throw TokenExpiredException when token is expired`() = runBlocking {
        // Given
        val token = "expired-token"
        val newPassword = "new-password"

        val userId = 1L
        val resetToken = PasswordResetToken(
            userId = userId,
            token = token,
            expiryDate = Instant.now().minusMillis(1000), // Expired token
            used = false
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken
        coJustRun { tokenRepository.deleteById(resetToken.id) }

        // When/Then
        assertThrows<TokenExpiredException> {
            passwordResetService.resetPassword(token, newPassword)
        }

        coVerify { tokenRepository.findByToken(token) }
        coVerify { tokenRepository.deleteById(resetToken.id) }
    }

    @Test
    fun `should throw InvalidTokenException when token is already used`() = runBlocking {
        // Given
        val token = "used-token"
        val newPassword = "new-password"

        val userId = 1L
        val resetToken = PasswordResetToken(
            userId = userId,
            token = token,
            expiryDate = Instant.now().plusMillis(tokenExpiration),
            used = true // Already used token
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken

        // When/Then
        assertThrows<InvalidTokenException> {
            passwordResetService.resetPassword(token, newPassword)
        }

        coVerify { tokenRepository.findByToken(token) }
    }

    @Test
    fun `should throw UserNotFoundException when user not found for token`() = runBlocking {
        // Given
        val token = "valid-token"
        val newPassword = "new-password"

        val userId = 1L
        val resetToken = PasswordResetToken(
            userId = userId,
            token = token,
            expiryDate = Instant.now().plusMillis(tokenExpiration),
            used = false
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken
        coEvery { userRepository.findById(userId) } returns null

        // When/Then
        assertThrows<UserNotFoundException> {
            passwordResetService.resetPassword(token, newPassword)
        }

        coVerify { tokenRepository.findByToken(token) }
        coVerify { userRepository.findById(userId) }
    }

    @Test
    fun `should validate token successfully`() = runBlocking {
        // Given
        val token = "valid-token"

        val resetToken = PasswordResetToken(
            userId = 2L,
            token = token,
            expiryDate = Instant.now().plusMillis(tokenExpiration),
            used = false
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken

        // When
        val isValid = passwordResetService.validateToken(token)

        // Then
        assertTrue(isValid)
        coVerify { tokenRepository.findByToken(token) }
    }

    @Test
    fun `should return false when validating non-existent token`() = runBlocking {
        // Given
        val token = "non-existent-token"

        coEvery { tokenRepository.findByToken(token) } returns null

        // When
        val isValid = passwordResetService.validateToken(token)

        // Then
        assertFalse(isValid)
        coVerify { tokenRepository.findByToken(token) }
    }

    @Test
    fun `should return false when validating expired token`() = runBlocking {
        // Given
        val token = "expired-token"

        val resetToken = PasswordResetToken(
            userId = 2L,
            token = token,
            expiryDate = Instant.now().minusMillis(1000), // Expired token
            used = false
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken
        coJustRun { tokenRepository.deleteById(resetToken.id) }

        // When
        val isValid = passwordResetService.validateToken(token)

        // Then
        assertFalse(isValid)
        coVerify { tokenRepository.findByToken(token) }
        coVerify { tokenRepository.deleteById(resetToken.id) }
    }

    @Test
    fun `should return false when validating used token`() = runBlocking {
        // Given
        val token = "used-token"

        val resetToken = PasswordResetToken(
            userId = 2L,
            token = token,
            expiryDate = Instant.now().plusMillis(tokenExpiration),
            used = true // Already used token
        )

        coEvery { tokenRepository.findByToken(token) } returns resetToken

        // When
        val isValid = passwordResetService.validateToken(token)

        // Then
        assertFalse(isValid)
        coVerify { tokenRepository.findByToken(token) }
    }
}
