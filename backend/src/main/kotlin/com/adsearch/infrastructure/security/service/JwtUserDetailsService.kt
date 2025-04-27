package com.adsearch.infrastructure.security.service


import com.adsearch.domain.port.spi.UserPersistencePort
import com.adsearch.infrastructure.security.model.JwtUserDetails
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
}
