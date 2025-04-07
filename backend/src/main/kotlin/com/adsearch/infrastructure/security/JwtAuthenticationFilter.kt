package com.adsearch.infrastructure.security

import com.adsearch.domain.port.AuthenticationPort
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter for JWT authentication
 */
@Component
class JwtAuthenticationFilter(
    @org.springframework.context.annotation.Lazy private val authenticationPort: AuthenticationPort
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

        val username: String? = authenticationPort.validateTokenAndGetUsername(jwt)
        if (username == null) {
            // validation failed or token expired
            filterChain.doFilter(request, response)
            return
        }

        try {
            val user = authenticationPort.loadUserByUsername(username)
            if (user == null) {
                filterChain.doFilter(request, response)
                return
            }
            
            val authorities = user.roles.map { SimpleGrantedAuthority(it) }
            val authentication = UsernamePasswordAuthenticationToken(user, null, authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            // set user details on spring security context
            SecurityContextHolder.getContext().authentication = authentication
        } catch (userNotFoundEx: UsernameNotFoundException) {
            // user not found
            filterChain.doFilter(request, response)
            return
        }

        // continue with authenticated user
        filterChain.doFilter(request, response)
    }
}
