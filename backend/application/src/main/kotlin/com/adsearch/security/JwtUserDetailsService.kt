package com.adsearch.security

import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtUserDetailsService::class.java)
    }

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
        if (user == null) {
            LOG.warn("user not found: {}", username)
            throw UsernameNotFoundException("UserEntity $username not found")
        }
        val authorities = user.roles.flatMap { listOf(SimpleGrantedAuthority(it.type)) }
        return User(username, user.password, authorities)
    }
}
