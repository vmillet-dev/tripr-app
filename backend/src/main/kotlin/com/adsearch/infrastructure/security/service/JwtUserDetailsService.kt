package com.adsearch.infrastructure.security.service

import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.infrastructure.security.model.JwtUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    @org.springframework.context.annotation.Lazy private val authenticationPort: AuthenticationPort
) : UserDetailsService {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)
    }

    override fun loadUserByUsername(username: String): JwtUserDetails {
        val user = authenticationPort.loadUserByUsername(username)
        if (user == null) {
            LOG.warn("user not found: {}", username)
            throw UsernameNotFoundException("User $username not found")
        }
        val authorities = user.roles.map { SimpleGrantedAuthority(it) }
        return JwtUserDetails(user.id, username, user.password, authorities)
    }

    suspend fun loadUserByUserId(userId: Long): JwtUserDetails {
        val user = authenticationPort.loadUserByUserId(userId)
        if (user == null) {
            LOG.warn("user id not found: {}", userId)
            throw UsernameNotFoundException("User $userId not found")
        }
        val authorities = user.roles.map { SimpleGrantedAuthority(it) }
        return JwtUserDetails(user.id, user.username, user.password, authorities)
    }
}
