package com.adsearch.application.impl

import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.command.LoginUserCommand
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.domain.port.out.ConfigPropertiesPort
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
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

@DisplayName("LoginUseCaseImpl Tests")
class LoginUseCaseImplTest {

    private val configProperties: ConfigPropertiesPort = mockk()
    private val authenticationService: AuthenticationServicePort = mockk()
    private val refreshTokenPersistence: RefreshTokenPersistencePort = mockk()
    private val jwtTokenService: JwtTokenServicePort = mockk()

    private lateinit var loginUseCase: LoginUseCaseImpl

    @BeforeEach
    fun setUp() {
        loginUseCase = LoginUseCaseImpl(
            configProperties,
            authenticationService,
            refreshTokenPersistence,
            jwtTokenService
        )
    }

    @Test
    @DisplayName("Should successfully login user with valid credentials")
    fun shouldSuccessfullyLoginUserWithValidCredentials() {
        // Given
        val command = LoginUserCommand("testuser", "password123")
        val user = UserDom(1L, "testuser", "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val accessToken = "access-token-123"
        val refreshTokenExpiration = 3600L

        every { authenticationService.authenticate(command.username, command.password) } returns user
        every { refreshTokenPersistence.deleteByUserId(user.id) } returns Unit
        every { configProperties.getRefreshTokenExpiration() } returns refreshTokenExpiration
        every { refreshTokenPersistence.save(any<RefreshTokenDom>()) } returns Unit
        every { jwtTokenService.createAccessToken(user) } returns accessToken

        // When
        val result = loginUseCase.login(command)

        // Then
        assertNotNull(result)
        assertEquals(accessToken, result.accessToken)
        assertNotNull(result.refreshToken)
        
        verify { authenticationService.authenticate(command.username, command.password) }
        verify { refreshTokenPersistence.deleteByUserId(user.id) }
        verify { configProperties.getRefreshTokenExpiration() }
        verify { refreshTokenPersistence.save(any<RefreshTokenDom>()) }
        verify { jwtTokenService.createAccessToken(user) }
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when authentication fails")
    fun shouldThrowInvalidCredentialsExceptionWhenAuthenticationFails() {
        // Given
        val command = LoginUserCommand("testuser", "wrongpassword")
        val authException = RuntimeException("Authentication failed")

        every { authenticationService.authenticate(command.username, command.password) } throws authException

        // When & Then
        val exception = assertThrows<InvalidCredentialsException> {
            loginUseCase.login(command)
        }

        assertEquals("Authentication failed for user ${command.username} - invalid credentials provided", exception.message)
        assertEquals(authException, exception.cause)
        
        verify { authenticationService.authenticate(command.username, command.password) }
        verify(exactly = 0) { refreshTokenPersistence.deleteByUserId(any()) }
        verify(exactly = 0) { jwtTokenService.createAccessToken(any()) }
    }

    @Test
    @DisplayName("Should clean up existing refresh tokens before creating new one")
    fun shouldCleanUpExistingRefreshTokensBeforeCreatingNewOne() {
        // Given
        val command = LoginUserCommand("testuser", "password123")
        val user = UserDom(1L, "testuser", "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val accessToken = "access-token-123"
        val refreshTokenExpiration = 3600L

        every { authenticationService.authenticate(command.username, command.password) } returns user
        every { refreshTokenPersistence.deleteByUserId(user.id) } returns Unit
        every { configProperties.getRefreshTokenExpiration() } returns refreshTokenExpiration
        every { refreshTokenPersistence.save(any<RefreshTokenDom>()) } returns Unit
        every { jwtTokenService.createAccessToken(user) } returns accessToken

        // When
        loginUseCase.login(command)

        // Then
        verify { refreshTokenPersistence.deleteByUserId(user.id) }
        verify { refreshTokenPersistence.save(any<RefreshTokenDom>()) }
    }

    @Test
    @DisplayName("Should create refresh token with correct expiry date")
    fun shouldCreateRefreshTokenWithCorrectExpiryDate() {
        // Given
        val command = LoginUserCommand("testuser", "password123")
        val user = UserDom(1L, "testuser", "test@example.com", "hashedPassword", setOf("ROLE_USER"), true)
        val accessToken = "access-token-123"
        val refreshTokenExpiration = 3600L
        val capturedRefreshToken = mutableListOf<RefreshTokenDom>()

        every { authenticationService.authenticate(command.username, command.password) } returns user
        every { refreshTokenPersistence.deleteByUserId(user.id) } returns Unit
        every { configProperties.getRefreshTokenExpiration() } returns refreshTokenExpiration
        every { refreshTokenPersistence.save(capture(capturedRefreshToken)) } returns Unit
        every { jwtTokenService.createAccessToken(user) } returns accessToken

        // When
        val beforeLogin = Instant.now()
        loginUseCase.login(command)
        val afterLogin = Instant.now()

        // Then
        val savedToken = capturedRefreshToken.first()
        assertEquals(user.id, savedToken.userId)
        assertEquals(false, savedToken.revoked)
        assert(savedToken.expiryDate.isAfter(beforeLogin.plusSeconds(refreshTokenExpiration - 1)))
        assert(savedToken.expiryDate.isBefore(afterLogin.plusSeconds(refreshTokenExpiration + 1)))
    }
}
