package com.adsearch.infrastructure.adapter.`in`.web.controller

import com.adsearch.application.LoginUseCase
import com.adsearch.application.LogoutUseCase
import com.adsearch.application.PasswordResetUseCase
import com.adsearch.application.RefreshTokenUseCase
import com.adsearch.application.RegisterUseCase
import com.adsearch.domain.auth.AuthResponse
import com.adsearch.domain.command.LoginUserCommand
import com.adsearch.domain.command.RegisterUserCommand
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.PasswordResetDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.PasswordResetRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.RegisterRequestDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    private lateinit var authController: AuthController
    private val loginUseCase = mockk<LoginUseCase>()
    private val registerUseCase = mockk<RegisterUseCase>()
    private val logoutUseCase = mockk<LogoutUseCase>()
    private val refreshTokenUseCase = mockk<RefreshTokenUseCase>()
    private val passwordResetUseCase = mockk<PasswordResetUseCase>()
    private val response = mockk<HttpServletResponse>(relaxed = true)
    private val request = mockk<HttpServletRequest>(relaxed = true)
    
    private val testCookieName = "refresh_token"
    private val testCookieMaxAge = 604800

    @BeforeEach
    fun setUp() {
        authController = AuthController(
            loginUseCase,
            registerUseCase,
            logoutUseCase,
            refreshTokenUseCase,
            passwordResetUseCase,
            testCookieName,
            testCookieMaxAge
        )
    }

    @Test
    @DisplayName("Should login user successfully when valid credentials are provided")
    fun shouldLoginUserSuccessfullyWhenValidCredentialsAreProvided() {
        // Given
        val authRequest = AuthRequestDto("testuser", "testpassword")
        val authResponse = AuthResponse("access-token", "refresh-token")
        val cookieSlot = slot<Cookie>()

        every { loginUseCase.login(any<LoginUserCommand>()) } returns authResponse
        every { response.addCookie(capture(cookieSlot)) } returns Unit

        // When
        val result = authController.login(authRequest, response)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("access-token", result.body?.accessToken)
        verify(exactly = 1) { loginUseCase.login(any<LoginUserCommand>()) }
        verify(exactly = 1) { response.addCookie(any<Cookie>()) }
        
        val capturedCookie = cookieSlot.captured
        assertEquals(testCookieName, capturedCookie.name)
        assertEquals("refresh-token", capturedCookie.value)
        assertEquals(testCookieMaxAge, capturedCookie.maxAge)
        assertTrue(capturedCookie.isHttpOnly)
    }

    @Test
    @DisplayName("Should login user without setting cookie when refresh token is null")
    fun shouldLoginUserWithoutSettingCookieWhenRefreshTokenIsNull() {
        // Given
        val authRequest = AuthRequestDto("testuser", "testpassword")
        val authResponse = AuthResponse("access-token", null)

        every { loginUseCase.login(any<LoginUserCommand>()) } returns authResponse

        // When
        val result = authController.login(authRequest, response)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("access-token", result.body?.accessToken)
        verify(exactly = 1) { loginUseCase.login(any<LoginUserCommand>()) }
        verify(exactly = 0) { response.addCookie(any<Cookie>()) }
    }

    @Test
    @DisplayName("Should register user successfully when valid data is provided")
    fun shouldRegisterUserSuccessfullyWhenValidDataIsProvided() {
        // Given
        val registerRequest = RegisterRequestDto("newuser", "new@example.com", "newpassword")

        every { registerUseCase.register(any<RegisterUserCommand>()) } returns Unit

        // When
        val result = authController.register(registerRequest)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("UserEntity registered successfully", result.body?.get("message"))
        verify(exactly = 1) { registerUseCase.register(any<RegisterUserCommand>()) }
    }

    @Test
    @DisplayName("Should refresh token successfully when valid refresh token cookie is provided")
    fun shouldRefreshTokenSuccessfullyWhenValidRefreshTokenCookieIsProvided() {
        // Given
        val refreshToken = "valid-refresh-token"
        val authResponse = AuthResponse("new-access-token", null)
        val cookies = arrayOf(Cookie(testCookieName, refreshToken))

        every { request.cookies } returns cookies
        every { refreshTokenUseCase.refreshAccessToken(refreshToken) } returns authResponse

        // When
        val result = authController.refreshToken(request)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("new-access-token", result.body?.accessToken)
        verify(exactly = 1) { refreshTokenUseCase.refreshAccessToken(refreshToken) }
    }

    @Test
    @DisplayName("Should handle refresh token request when no cookies are present")
    fun shouldHandleRefreshTokenRequestWhenNoCookiesArePresent() {
        // Given
        val authResponse = AuthResponse("new-access-token", null)

        every { request.cookies } returns null
        every { refreshTokenUseCase.refreshAccessToken(null) } returns authResponse

        // When
        val result = authController.refreshToken(request)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("new-access-token", result.body?.accessToken)
        verify(exactly = 1) { refreshTokenUseCase.refreshAccessToken(null) }
    }

    @Test
    @DisplayName("Should logout user successfully when refresh token cookie is provided")
    fun shouldLogoutUserSuccessfullyWhenRefreshTokenCookieIsProvided() {
        // Given
        val refreshToken = "logout-refresh-token"
        val cookies = arrayOf(Cookie(testCookieName, refreshToken))
        val cookieSlot = slot<Cookie>()

        every { request.cookies } returns cookies
        every { logoutUseCase.logout(refreshToken) } returns Unit
        every { response.addCookie(capture(cookieSlot)) } returns Unit

        // When
        val result = authController.logout(request, response)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Logged out successfully", result.body?.get("message"))
        verify(exactly = 1) { logoutUseCase.logout(refreshToken) }
        verify(exactly = 1) { response.addCookie(any<Cookie>()) }
        
        val capturedCookie = cookieSlot.captured
        assertEquals(testCookieName, capturedCookie.name)
        assertEquals("", capturedCookie.value)
        assertEquals(0, capturedCookie.maxAge)
    }

    @Test
    @DisplayName("Should logout user when no cookies are present")
    fun shouldLogoutUserWhenNoCookiesArePresent() {
        // Given
        val cookieSlot = slot<Cookie>()

        every { request.cookies } returns null
        every { logoutUseCase.logout(null) } returns Unit
        every { response.addCookie(capture(cookieSlot)) } returns Unit

        // When
        val result = authController.logout(request, response)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Logged out successfully", result.body?.get("message"))
        verify(exactly = 1) { logoutUseCase.logout(null) }
        verify(exactly = 1) { response.addCookie(any<Cookie>()) }
    }

    @Test
    @DisplayName("Should request password reset successfully when username is provided")
    fun shouldRequestPasswordResetSuccessfullyWhenUsernameIsProvided() {
        // Given
        val passwordResetRequest = PasswordResetRequestDto("resetuser")

        every { passwordResetUseCase.requestPasswordReset("resetuser") } returns Unit

        // When
        val result = authController.requestPasswordReset(passwordResetRequest)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("If the username exists, a password reset email has been sent", result.body?.get("message"))
        verify(exactly = 1) { passwordResetUseCase.requestPasswordReset("resetuser") }
    }

    @Test
    @DisplayName("Should reset password successfully when valid token and password are provided")
    fun shouldResetPasswordSuccessfullyWhenValidTokenAndPasswordAreProvided() {
        // Given
        val passwordResetDto = PasswordResetDto("reset-token", "newpassword")

        every { passwordResetUseCase.resetPassword("reset-token", "newpassword") } returns Unit

        // When
        val result = authController.resetPassword(passwordResetDto)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Password has been reset successfully", result.body?.get("message"))
        verify(exactly = 1) { passwordResetUseCase.resetPassword("reset-token", "newpassword") }
    }

    @Test
    @DisplayName("Should validate token successfully when valid token is provided")
    fun shouldValidateTokenSuccessfullyWhenValidTokenIsProvided() {
        // Given
        val token = "valid-token"

        every { passwordResetUseCase.validateToken(token) } returns true

        // When
        val result = authController.validateToken(token)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(true, result.body?.get("valid"))
        verify(exactly = 1) { passwordResetUseCase.validateToken(token) }
    }

    @Test
    @DisplayName("Should validate token as invalid when invalid token is provided")
    fun shouldValidateTokenAsInvalidWhenInvalidTokenIsProvided() {
        // Given
        val token = "invalid-token"

        every { passwordResetUseCase.validateToken(token) } returns false

        // When
        val result = authController.validateToken(token)

        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(false, result.body?.get("valid"))
        verify(exactly = 1) { passwordResetUseCase.validateToken(token) }
    }

    @Test
    @DisplayName("Should create correct login command from request")
    fun shouldCreateCorrectLoginCommandFromRequest() {
        // Given
        val authRequest = AuthRequestDto("commanduser", "commandpassword")
        val authResponse = AuthResponse("command-token", null)
        val commandSlot = slot<LoginUserCommand>()

        every { loginUseCase.login(capture(commandSlot)) } returns authResponse

        // When
        authController.login(authRequest, response)

        // Then
        val capturedCommand = commandSlot.captured
        assertEquals("commanduser", capturedCommand.username)
        assertEquals("commandpassword", capturedCommand.password)
    }

    @Test
    @DisplayName("Should create correct register command from request")
    fun shouldCreateCorrectRegisterCommandFromRequest() {
        // Given
        val registerRequest = RegisterRequestDto("reguser", "regpassword", "reg@example.com")
        val commandSlot = slot<RegisterUserCommand>()

        every { registerUseCase.register(capture(commandSlot)) } returns Unit

        // When
        authController.register(registerRequest)

        // Then
        val capturedCommand = commandSlot.captured
        assertEquals("reguser", capturedCommand.username)
        assertEquals("regpassword", capturedCommand.password)
        assertEquals("reg@example.com", capturedCommand.email)
    }
}
