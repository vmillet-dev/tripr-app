package com.adsearch.infrastructure.security.service

import com.adsearch.domain.port.AuthenticationPort
import com.adsearch.infrastructure.adapter.out.security.mapper.JwtUserDetailsMapper
import com.adsearch.infrastructure.security.model.JwtUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    @Lazy private val authenticationPort: AuthenticationPort,
    private val jwtUserDetailsMapper: JwtUserDetailsMapper
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
        return jwtUserDetailsMapper.toJwtUserDetails(user)
    }

    suspend fun loadUserByUserId(userId: Long): JwtUserDetails {
        val user = authenticationPort.loadUserByUserId(userId)
        if (user == null) {
            LOG.warn("user id not found: {}", userId)
            throw UsernameNotFoundException("User $userId not found")
        }
        return jwtUserDetailsMapper.toJwtUserDetails(user)
    }
}
