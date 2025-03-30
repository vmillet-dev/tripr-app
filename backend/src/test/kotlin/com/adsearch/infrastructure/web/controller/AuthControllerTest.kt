package com.adsearch.infrastructure.web.controller

import com.adsearch.application.port.AuthenticationUseCase
import com.adsearch.application.service.AuthenticationService
import com.adsearch.application.service.RefreshTokenService
import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.AuthResponse
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.infrastructure.web.dto.AuthRequestDto
import com.adsearch.infrastructure.web.dto.RegisterRequestDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.time.Instant

class AuthControllerTest {
    
    private lateinit var authController: AuthController
    private lateinit var authenticationUseCase: AuthenticationUseCase
    private lateinit var authenticationService: AuthenticationService
    private lateinit var refreshTokenService: RefreshTokenService
    private lateinit var response: HttpServletResponse
    
    @BeforeEach
    fun setUp() {
        authenticationUseCase = mockk()
        authenticationService = mockk()
        refreshTokenService = mockk()
        response = mockk(relaxed = true)
        
        authController = AuthController(
            authenticationUseCase,
            authenticationService,
            refreshTokenService
        )
    }
    
    @Test
    fun `should login successfully`() = runBlocking {
        // Given
        val authRequestDto = AuthRequestDto(
            username = "testuser",
            password = "password"
        )
        
        val authResponse = AuthResponse(
            accessToken = "test-access-token",
            username = "testuser",
            roles = mutableListOf("USER")
        )
        
        val user = User(
            username = "testuser",
            password = "",
            roles = mutableListOf("USER")
        )
        
        val refreshToken = RefreshToken(
            userId = user.id,
            token = "test-refresh-token",
            expiryDate = Instant.now().plusSeconds(604800)
        )
        
        coEvery { 
            authenticationUseCase.authenticate(any()) 
        } returns authResponse
        
        coEvery { 
            refreshTokenService.createRefreshToken(any()) 
        } returns refreshToken
        
        every { 
            authenticationService.addRefreshTokenCookie(any(), any()) 
        } returns Unit
        
        // When
        val result = authController.login(authRequestDto, response)
        
        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(authResponse.accessToken, result.body?.accessToken)
        assertEquals(authResponse.username, result.body?.username)
        assertEquals(authResponse.roles, result.body?.roles)
        
        coVerify { authenticationUseCase.authenticate(any()) }
        coVerify { refreshTokenService.createRefreshToken(any()) }
        verify { authenticationService.addRefreshTokenCookie(response, refreshToken.token) }
    }
    
    @Test
    fun `should refresh token successfully`() = runBlocking {
        // Given
        val request = mockk<HttpServletRequest>()
        
        val authResponse = AuthResponse(
            accessToken = "new-access-token",
            username = "testuser",
            roles = mutableListOf("USER")
        )
        
        coEvery { 
            authenticationUseCase.refreshToken(request, response) 
        } returns authResponse
        
        // When
        val result = authController.refreshToken(request, response)
        
        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(authResponse.accessToken, result.body?.accessToken)
        assertEquals(authResponse.username, result.body?.username)
        assertEquals(authResponse.roles, result.body?.roles)
        
        coVerify { authenticationUseCase.refreshToken(request, response) }
    }
    
    @Test
    fun `should logout successfully`() = runBlocking {
        // Given
        val request = mockk<HttpServletRequest>()
        
        coEvery { 
            authenticationUseCase.logout(request, response) 
        } returns Unit
        
        // When
        val result = authController.logout(request, response)
        
        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("Logged out successfully", result.body?.get("message"))
        
        coVerify { authenticationUseCase.logout(request, response) }
    }
    
    @Test
    fun `should register user successfully`() = runBlocking {
        // Given
        val registerRequestDto = RegisterRequestDto(
            username = "newuser",
            password = "password",
            email = "newuser@example.com"
        )
        
        val authRequestSlot = slot<AuthRequest>()
        val emailSlot = slot<String>()
        
        coEvery { 
            authenticationUseCase.register(capture<AuthRequest>(authRequestSlot), capture<String>(emailSlot))
        } returns Unit
        
        // When
        val result = authController.register(registerRequestDto)
        
        // Then
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals("User registered successfully", result.body?.get("message"))
        
        assertEquals("newuser", authRequestSlot.captured.username)
        assertEquals("password", authRequestSlot.captured.password)
        assertEquals("newuser@example.com", emailSlot.captured)
        
        coVerify { authenticationUseCase.register(any(), any()) }
    }
}
