package com.adsearch.application.service

import com.adsearch.common.exception.InvalidCredentialsException
import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.UserNotFoundException
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

class AuthenticationServiceTest {

    private lateinit var authenticationService: AuthenticationService
    private lateinit var userRepository: UserPersistencePort
    private lateinit var jwtService: JwtService
    private lateinit var refreshTokenService: RefreshTokenService
    private lateinit var passwordEncoder: PasswordEncoder

    private val refreshTokenCookieName = "refresh-token"
    private val refreshTokenExpiration = 604800000L

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        jwtService = mockk()
        refreshTokenService = mockk()
        passwordEncoder = mockk()

        // Mock save operation for userRepository
        coEvery { userRepository.save(any()) } returns mockk()

        authenticationService = AuthenticationService(
            userRepository,
            jwtService,
            refreshTokenService,
            passwordEncoder,
            refreshTokenCookieName,
            refreshTokenExpiration
        )
    }

    @Test
    fun `should authenticate user with valid credentials`() = runBlocking {
        // Given
        val username = "testuser"
        val password = "password"
        val encodedPassword = "encoded-password"

        val authRequest = AuthRequest(username, password)

        val user = User(
            id = 2L,
            username = username,
            password = encodedPassword,
            roles = mutableListOf("USER")
        )

        val accessToken = "test-access-token"

        coEvery { userRepository.findByUsername(username) } returns user
        every { passwordEncoder.matches(password, encodedPassword) } returns true
        every { jwtService.generateToken(user) } returns accessToken

        // When
        val result = authenticationService.authenticate(authRequest)

        // Then
        assertNotNull(result)
        assertEquals(accessToken, result.accessToken)
        assertEquals(username, result.username)
        assertEquals(user.roles, result.roles)

        coVerify { userRepository.findByUsername(username) }
        verify { passwordEncoder.matches(password, encodedPassword) }
        verify { jwtService.generateToken(user) }
    }

    @Test
    fun `should throw exception when user not found`() = runBlocking {
        // Given
        val username = "nonexistent"
        val password = "password"

        val authRequest = AuthRequest(username, password)

        coEvery { userRepository.findByUsername(username) } returns null

        // When/Then
        assertThrows<UserNotFoundException> {
            runBlocking {
                authenticationService.authenticate(authRequest)
            }
        }

        coVerify { userRepository.findByUsername(username) }
    }

    @Test
    fun `should throw exception when password is invalid`() = runBlocking {
        // Given
        val username = "testuser"
        val password = "wrong-password"
        val encodedPassword = "encoded-password"

        val authRequest = AuthRequest(username, password)

        val user = User(
            id = 2L,
            username = username,
            password = encodedPassword,
            roles = mutableListOf("USER")
        )

        coEvery { userRepository.findByUsername(username) } returns user
        every { passwordEncoder.matches(password, encodedPassword) } returns false

        // When/Then
        assertThrows<InvalidCredentialsException> {
            runBlocking {
                authenticationService.authenticate(authRequest)
            }
        }

        coVerify { userRepository.findByUsername(username) }
        verify { passwordEncoder.matches(password, encodedPassword) }
    }

    @Test
    fun `should refresh token successfully`() = runBlocking {
        // Given
        val refreshTokenValue = "test-refresh-token"
        val userId = 1L

        val refreshToken = RefreshToken(
            id = 2L,
            userId = userId,
            token = refreshTokenValue,
            expiryDate = Instant.now().plusSeconds(3600)
        )

        val user = User(
            id = userId,
            username = "testuser",
            password = "encoded-password",
            roles = mutableListOf("USER")
        )

        val newRefreshToken = RefreshToken(
            id = 2L,
            userId = userId,
            token = "new-refresh-token",
            expiryDate = Instant.now().plusSeconds(3600)
        )

        val accessToken = "new-access-token"

        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>(relaxed = true)
        val cookies = arrayOf(Cookie(refreshTokenCookieName, refreshTokenValue))

        every { request.cookies } returns cookies
        coEvery { refreshTokenService.findByToken(refreshTokenValue) } returns refreshToken
        coEvery { refreshTokenService.verifyExpiration(refreshToken) } returns refreshToken
        coEvery { userRepository.findById(userId) } returns user
        coEvery { userRepository.findAll() } returns listOf(user)
        every { jwtService.generateToken(user) } returns accessToken
        coEvery { refreshTokenService.createRefreshToken(user) } returns newRefreshToken

        // When
        val result = authenticationService.refreshToken(request, response)

        // Then
        assertNotNull(result)
        assertEquals(accessToken, result.accessToken)
        assertEquals(user.username, result.username)
        assertEquals(user.roles, result.roles)

        verify { request.cookies }
        coVerify { refreshTokenService.findByToken(refreshTokenValue) }
        coVerify { refreshTokenService.verifyExpiration(refreshToken) }
        coVerify { userRepository.findById(userId) }
        verify { jwtService.generateToken(user) }
        coVerify { refreshTokenService.createRefreshToken(user) }
    }

    @Test
    fun `should throw exception when refresh token is missing`() = runBlocking {
        // Given
        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>()

        every { request.cookies } returns null

        // When/Then
        assertThrows<InvalidTokenException> {
            runBlocking {
                authenticationService.refreshToken(request, response)
            }
        }

        verify { request.cookies }
    }

    @Test
    fun `should logout successfully`() = runBlocking {
        // Given
        val refreshTokenValue = "test-refresh-token"
        val userId = 1L

        val refreshToken = RefreshToken(
            id = 2L,
            userId = userId,
            token = refreshTokenValue,
            expiryDate = Instant.now().plusSeconds(3600)
        )

        val request = mockk<HttpServletRequest>()
        val response = mockk<HttpServletResponse>(relaxed = true)
        val cookies = arrayOf(Cookie(refreshTokenCookieName, refreshTokenValue))
        val cookieSlot = slot<Cookie>()

        every { request.cookies } returns cookies
        coEvery { refreshTokenService.findByToken(refreshTokenValue) } returns refreshToken
        coEvery { refreshTokenService.deleteByUserId(userId) } returns Unit
        every { response.addCookie(capture(cookieSlot)) } returns Unit

        // When
        authenticationService.logout(request, response)

        // Then
        verify { request.cookies }
        coVerify { refreshTokenService.findByToken(refreshTokenValue) }
        coVerify { refreshTokenService.deleteByUserId(userId) }
        verify { response.addCookie(any()) }

        // Verify cookie is cleared
        assertEquals(refreshTokenCookieName, cookieSlot.captured.name)
        assertEquals("", cookieSlot.captured.value)
        assertEquals(0, cookieSlot.captured.maxAge)
        assertEquals(true, cookieSlot.captured.isHttpOnly)
    }

    @Test
    fun `should get current user from valid token`() = runBlocking {
        // Given
        val userId = 1L
        val token = "valid-token"

        val user = User(
            id = userId,
            username = "testuser",
            password = "encoded-password",
            roles = mutableListOf("USER")
        )

        val request = mockk<HttpServletRequest>()

        every { request.getHeader("Authorization") } returns "Bearer $token"
        every { jwtService.validateToken(token) } returns true
        every { jwtService.getUserIdFromToken(token) } returns userId
        coEvery { userRepository.findById(userId) } returns user

        // When
        val result = authenticationService.getCurrentUser(request)

        // Then
        assertEquals(user, result)

        verify { request.getHeader("Authorization") }
        verify { jwtService.validateToken(token) }
        verify { jwtService.getUserIdFromToken(token) }
        coVerify { userRepository.findById(userId) }
    }

    @Test
    fun `should return null when authorization header is missing`() = runBlocking {
        // Given
        val request = mockk<HttpServletRequest>()

        every { request.getHeader("Authorization") } returns null

        // When
        val result = authenticationService.getCurrentUser(request)

        // Then
        assertEquals(null, result)

        verify { request.getHeader("Authorization") }
    }

    @Test
    fun `should return null when token is invalid`() = runBlocking {
        // Given
        val token = "invalid-token"

        val request = mockk<HttpServletRequest>()

        every { request.getHeader("Authorization") } returns "Bearer $token"
        every { jwtService.validateToken(token) } returns false

        // When
        val result = authenticationService.getCurrentUser(request)

        // Then
        assertEquals(null, result)

        verify { request.getHeader("Authorization") }
        verify { jwtService.validateToken(token) }
    }
}
