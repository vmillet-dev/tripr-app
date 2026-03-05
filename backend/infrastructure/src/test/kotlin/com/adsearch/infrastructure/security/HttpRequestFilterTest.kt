package com.adsearch.infrastructure.security

import com.adsearch.domain.port.out.authentication.TokenGeneratorPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder

class HttpRequestFilterTest {

    private val tokenGenerator = mockk<TokenGeneratorPort>()
    private val filter = object : HttpRequestFilter(tokenGenerator) {
        fun doFilterPublic(req: HttpServletRequest, res: HttpServletResponse, chain: FilterChain) {
            super.doFilterInternal(req, res, chain)
        }
    }

    private val request = mockk<HttpServletRequest>(relaxed = true)
    private val response = mockk<HttpServletResponse>(relaxed = true)
    private val filterChain = mockk<FilterChain>(relaxed = true)

    @BeforeEach
    fun setUp() {
        SecurityContextHolder.clearContext()
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `doFilterInternal should proceed without authentication when no header present`() {
        // given
        every { request.getHeader(HttpHeaders.AUTHORIZATION) } returns null

        // when
        filter.doFilterPublic(request, response, filterChain)

        // then
        verify { filterChain.doFilter(request, response) }
        assert(SecurityContextHolder.getContext().authentication == null)
    }

    @Test
    fun `doFilterInternal should authenticate when valid Bearer token present`() {
        // given
        val mockRequest = mockk<HttpServletRequest>(relaxed = true)
        every { mockRequest.method } returns "GET"
        every { mockRequest.requestURI } returns "/api/test"
        every { mockRequest.remoteAddr } returns "127.0.0.1"
        every { mockRequest.getHeader("User-Agent") } returns "JUnit"
        every { mockRequest.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer valid-token"

        every { tokenGenerator.validateAccessTokenAndGetUsername("valid-token") } returns "john"
        every { tokenGenerator.getAuthoritiesFromToken("valid-token") } returns listOf("ROLE_USER")

        // when
        filter.doFilterPublic(mockRequest, response, filterChain)

        // then
        verify { filterChain.doFilter(mockRequest, response) }
        val auth = SecurityContextHolder.getContext().authentication
        assert(auth != null)
        assert(auth?.name == "john")
    }

    @Test
    fun `doFilterInternal should throw TokenExpiredException when token is invalid`() {
        // given
        val mockRequest = mockk<HttpServletRequest>(relaxed = true)
        every { mockRequest.method } returns "GET"
        every { mockRequest.requestURI } returns "/api/test"
        every { mockRequest.remoteAddr } returns "127.0.0.1"
        every { mockRequest.getHeader("User-Agent") } returns "JUnit"
        every { mockRequest.getHeader(HttpHeaders.AUTHORIZATION) } returns "Bearer invalid-token"

        every { tokenGenerator.validateAccessTokenAndGetUsername("invalid-token") } returns null

        // when / then
        try {
            filter.doFilterPublic(mockRequest, response, filterChain)
            assert(false) { "Should have thrown TokenExpiredException" }
        } catch (e: Throwable) {
            // Depending on how OncePerRequestFilter handles it, it might be wrapped
            var current: Throwable? = e
            var found = false
            while (current != null) {
                if (current is com.adsearch.domain.exception.TokenExpiredException) {
                    found = true
                    break
                }
                current = current.cause
            }
            assert(found) { "Expected TokenExpiredException not found in $e" }
        }
    }
}
