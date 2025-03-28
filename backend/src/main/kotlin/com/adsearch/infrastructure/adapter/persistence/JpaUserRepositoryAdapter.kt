package com.adsearch.infrastructure.adapter.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.repository.UserRepositoryPort
import com.adsearch.infrastructure.repository.JpaUserRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

/**
 * Adapter implementation for UserRepositoryPort that delegates to the original JpaUserRepository
 * This follows the adapter pattern in hexagonal architecture
 */
@Repository
@Primary
class JpaUserRepositoryAdapter(private val jpaUserRepository: JpaUserRepository) : UserRepositoryPort {
    
    override suspend fun findByUsername(username: String): User? {
        return jpaUserRepository.findByUsername(username)
    }
    
    override suspend fun findById(id: Long): User? {
        return jpaUserRepository.findById(id)
    }
    
    override suspend fun save(user: User): User {
        return jpaUserRepository.save(user)
    }
    
    override suspend fun findAll(): List<User> {
        return jpaUserRepository.findAll()
    }
}
