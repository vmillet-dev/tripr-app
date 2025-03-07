package com.adsearch.infrastructure.security

import com.adsearch.application.service.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    
    private val logger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No Authorization header or not Bearer token")
            filterChain.doFilter(request, response)
            return
        }
        
        val jwt = authHeader.substring(7)
        logger.debug("Processing JWT token")
        
        try {
            if (jwtService.validateToken(jwt)) {
                val username = jwtService.getUsernameFromToken(jwt)
                val roles = jwtService.getRolesFromToken(jwt)
                
                logger.debug("Valid token for user: $username with roles: $roles")
                
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
                logger.debug("Authentication set in SecurityContext")
            } else {
                logger.debug("Invalid token")
            }
        } catch (e: Exception) {
            logger.error("Error processing JWT token", e)
            SecurityContextHolder.clearContext()
        }
        
        filterChain.doFilter(request, response)
    }
}
