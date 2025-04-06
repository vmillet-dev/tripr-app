package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserDetailsPort
import com.adsearch.domain.port.UserPersistencePort
import org.springframework.stereotype.Component

@Component
class UserDetailsAdapter(
    private val userPersistencePort: UserPersistencePort
) : UserDetailsPort {
    
    override fun loadUserByUsername(username: String): User? {
        return userPersistencePort.findByUsername(username)
    }
    
    override suspend fun loadUserByUserId(userId: Long): User? {
        return userPersistencePort.findById(userId)
    }
}
