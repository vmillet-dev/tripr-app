package com.adsearch.security

import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.port.out.security.TokenGeneratorPort
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter for JWT authentication
 */
@Component
class JwtAuthenticationFilter(
    private val tokenGenerator: TokenGeneratorPort
) : OncePerRequestFilter() {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val method = request.method
        val uri = request.requestURI
        val ip = request.remoteAddr
        val userAgent = request.getHeader("User-Agent") ?: "unknown"

        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.info("$method $uri | User: anonymous | IP: $ip | UA: $userAgent")

            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        LOG.debug("Processing JWT token")

        val username: String =
            tokenGenerator.validateAccessTokenAndGetUsername(jwt) ?: throw TokenExpiredException("Access token expired")

        val roles = tokenGenerator.getAuthoritiesFromToken(jwt)
        val authorities = roles.map { SimpleGrantedAuthority(it) }
        val userDetails = User(username, "", authorities)

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        // set user details on spring security context
        SecurityContextHolder.getContext().authentication = authentication

        // continue with authenticated user
        LOG.info("$method $uri | User: $username | IP: $ip | UA: $userAgent")
        filterChain.doFilter(request, response)
    }
}
