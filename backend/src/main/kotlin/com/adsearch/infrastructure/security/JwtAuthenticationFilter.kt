package com.adsearch.infrastructure.security

import com.adsearch.application.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter for JWT authentication
 */
@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    }


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        LOG.debug("Processing JWT token")

        try {
            if (jwtService.validateToken(jwt)) {
                val username = jwtService.getUsernameFromToken(jwt)
                val roles = jwtService.getRolesFromToken(jwt)

                LOG.debug("Valid token for user: {} with roles: {}", username, roles)

                // Create authorities with both formats: with and without ROLE_ prefix
                val authorities = roles.flatMap {
                    listOf(
                        SimpleGrantedAuthority(it),
                        SimpleGrantedAuthority("ROLE_$it")
                    )
                }

                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
                )

                SecurityContextHolder.getContext().authentication = authentication
                LOG.debug("Authentication set in SecurityContext")
            } else {
                LOG.debug("Invalid token")
            }
        } catch (e: Exception) {
            LOG.error("Error processing JWT token", e)
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}
