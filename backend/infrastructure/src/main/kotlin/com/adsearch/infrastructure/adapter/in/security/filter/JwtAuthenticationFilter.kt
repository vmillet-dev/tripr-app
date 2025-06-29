package com.adsearch.infrastructure.adapter.`in`.security.filter

import com.adsearch.domain.port.`in`.JwtTokenServicePort
import com.adsearch.infrastructure.service.JwtUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter for JWT authentication
 */
@Component
class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenServicePort,
    private val jwtUserDetailsService: JwtUserDetailsService
) : OncePerRequestFilter() {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        LOG.debug("Processing JWT token")

        val username: String? = jwtTokenService.validateAccessTokenAndGetUsername(jwt)
        if (username == null) {
            // validation failed or token expired
            filterChain.doFilter(request, response)
            return
        }

        val userDetails: UserDetails
        try {
            userDetails = jwtUserDetailsService.loadUserByUsername(username)
        } catch (_: UsernameNotFoundException) {
            // user not found
            LOG.warn("User $username not found")
            filterChain.doFilter(request, response)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        // set user details on spring security context
        SecurityContextHolder.getContext().authentication = authentication

        // continue with authenticated user
        filterChain.doFilter(request, response)
    }
}
