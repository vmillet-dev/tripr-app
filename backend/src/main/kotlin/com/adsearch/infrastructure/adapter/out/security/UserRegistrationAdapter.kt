package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.AuthRequest
import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.domain.port.UserRegistrationPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserRegistrationAdapter(
    private val passwordEncoder: PasswordEncoder,
    private val userPersistencePort: UserPersistencePort
) : UserRegistrationPort {
    
    override suspend fun register(authRequest: AuthRequest) {
        val user = User(
            username = authRequest.username,
            password = passwordEncoder.encode(authRequest.password),
            roles = mutableListOf("USER")
        )
        
        userPersistencePort.save(user)
    }
}
