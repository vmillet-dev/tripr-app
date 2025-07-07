package com.adsearch.infrastructure.adapter.`in`.security

import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.infrastructure.service.JwtUserDetailsService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("JWT Authentication Filter Tests")
class JwtAuthenticationFilterTest {

    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    private val jwtTokenService = mockk<JwtTokenServicePort>()
    private val jwtUserDetailsService = mockk<JwtUserDetailsService>()
    private val request = mockk<HttpServletRequest>(relaxed = true)
    private val response = mockk<HttpServletResponse>(relaxed = true)
    private val filterChain = mockk<FilterChain>(relaxed = true)

    @BeforeEach
    fun setUp() {
        jwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenService, jwtUserDetailsService)
        SecurityContextHolder.clearContext()
        
        // Setup common request mocks
        every { request.method } returns "GET"
        every { request.requestURI } returns "/api/test"
        every { request.remoteAddr } returns "127.0.0.1"
        every { request.getHeader("User-Agent") } returns "Test-Agent"
        
        // Mock request attributes for OncePerRequestFilter
        every { request.getAttribute(any()) } returns null
        every { request.setAttribute(any(), any()) } returns Unit
    }

    @Test
    @DisplayName("Should continue filter chain when no authorization header is present")
    fun shouldContinueFilterChainWhenNoAuthorizationHeaderIsPresent() {
        // Given
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns null

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        verify(exactly = 1) { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    @DisplayName("Should continue filter chain when authorization header does not start with Bearer")
    fun shouldContinueFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer() {
        // Given
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns "Basic dGVzdDp0ZXN0"

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        verify(exactly = 1) { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    @DisplayName("Should throw TokenExpiredException when JWT token is invalid")
    fun shouldThrowTokenExpiredExceptionWhenJwtTokenIsInvalid() {
        // Given
        val invalidToken = "invalid.jwt.token"
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer $invalidToken"
        every { jwtTokenService.validateAccessTokenAndGetUsername(invalidToken) } returns null

        // When & Then
        assertThrows<TokenExpiredException> {
            jwtAuthenticationFilter.doFilter(request, response, filterChain)
        }
        
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(invalidToken) }
    }

    @Test
    @DisplayName("Should continue filter chain when user is not found")
    fun shouldContinueFilterChainWhenUserIsNotFound() {
        // Given
        val validToken = "valid.jwt.token"
        val username = "nonexistentuser"
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer $validToken"
        every { jwtTokenService.validateAccessTokenAndGetUsername(validToken) } returns username
        every { jwtUserDetailsService.loadUserByUsername(username) } throws UsernameNotFoundException("User not found")

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        verify(exactly = 1) { filterChain.doFilter(request, response) }
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(validToken) }
        verify(exactly = 1) { jwtUserDetailsService.loadUserByUsername(username) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    @DisplayName("Should set authentication context when valid JWT token and user are provided")
    fun shouldSetAuthenticationContextWhenValidJwtTokenAndUserAreProvided() {
        // Given
        val validToken = "valid.jwt.token"
        val username = "testuser"
        val userDetails: UserDetails = User.builder()
            .username(username)
            .password("password")
            .authorities(SimpleGrantedAuthority("USER"))
            .build()

        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer $validToken"
        every { jwtTokenService.validateAccessTokenAndGetUsername(validToken) } returns username
        every { jwtUserDetailsService.loadUserByUsername(username) } returns userDetails

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        verify(exactly = 1) { filterChain.doFilter(request, response) }
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(validToken) }
        verify(exactly = 1) { jwtUserDetailsService.loadUserByUsername(username) }
        
        val authentication = SecurityContextHolder.getContext().authentication
        assertNotNull(authentication)
        assertEquals(userDetails, authentication.principal)
        assertEquals(1, authentication.authorities.size)
    }

    @Test
    @DisplayName("Should extract JWT token correctly from Bearer authorization header")
    fun shouldExtractJwtTokenCorrectlyFromBearerAuthorizationHeader() {
        // Given
        val jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token"
        val username = "extractuser"
        val userDetails: UserDetails = User.builder()
            .username(username)
            .password("password")
            .authorities(SimpleGrantedAuthority("USER"))
            .build()

        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer $jwtToken"
        every { jwtTokenService.validateAccessTokenAndGetUsername(jwtToken) } returns username
        every { jwtUserDetailsService.loadUserByUsername(username) } returns userDetails

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        verify(exactly = 1) { jwtTokenService.validateAccessTokenAndGetUsername(jwtToken) }
        assertNotNull(SecurityContextHolder.getContext().authentication)
    }

    @Test
    @DisplayName("Should handle multiple authorities correctly")
    fun shouldHandleMultipleAuthoritiesCorrectly() {
        // Given
        val validToken = "multi.auth.token"
        val username = "multiuser"
        val userDetails: UserDetails = User.builder()
            .username(username)
            .password("password")
            .authorities(SimpleGrantedAuthority("USER"), SimpleGrantedAuthority("ADMIN"))
            .build()

        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer $validToken"
        every { jwtTokenService.validateAccessTokenAndGetUsername(validToken) } returns username
        every { jwtUserDetailsService.loadUserByUsername(username) } returns userDetails

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        val authentication = SecurityContextHolder.getContext().authentication
        assertNotNull(authentication)
        assertEquals(2, authentication.authorities.size)
        assertEquals(userDetails, authentication.principal)
    }

    @Test
    @DisplayName("Should handle null User-Agent header gracefully")
    fun shouldHandleNullUserAgentHeaderGracefully() {
        // Given
        every { request.getHeader("User-Agent") } returns null
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns null

        // When
        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        // Then
        verify(exactly = 1) { filterChain.doFilter(request, response) }
        assertNull(SecurityContextHolder.getContext().authentication)
    }
}
