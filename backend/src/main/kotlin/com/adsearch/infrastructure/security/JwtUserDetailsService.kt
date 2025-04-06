package com.adsearch.infrastructure.security


import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.model.JwtUserDetails
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class JwtUserDetailsService(
    private val userPersistencePort: UserPersistencePort
) : UserDetailsService {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)
    }

    override fun loadUserByUsername(username: String): JwtUserDetails {
        val user = userPersistencePort.findByUsername(username)
        if (user == null) {
            LOG.warn("user not found: {}", username)
            throw UsernameNotFoundException("User $username not found")
        }
        val authorities = user.roles.flatMap { listOf(SimpleGrantedAuthority(it)) }
        return JwtUserDetails(user.id, username, user.password, authorities)
    }

    suspend fun loadUserByUserId(userId: Long): JwtUserDetails {
        val user = userPersistencePort.findById(userId)
        if (user == null) {
            LOG.warn("user id not found: {}", userId)
            throw UsernameNotFoundException("User $userId not found")
        }
        val authorities = user.roles.flatMap { listOf(SimpleGrantedAuthority(it)) }
        return JwtUserDetails(user.id, user.username, user.password, authorities)
    }
}
