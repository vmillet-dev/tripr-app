package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import com.adsearch.infrastructure.service.AuthenticationService
import org.springframework.stereotype.Component

@Component
class AuthenticationServiceAdapter(
    private val authenticationService: AuthenticationService,
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper
) : AuthenticationServicePort {

    override fun authenticate(username: String, password: String): UserDom {
        val username: String = authenticationService.authenticate(username, password)
        return userEntityMapper.toDomain(userRepository.findByUsername(username)!!)
    }

    override fun generateHashedPassword(password: String): String {
        return authenticationService.generateHashedPassword(password)
    }
}
