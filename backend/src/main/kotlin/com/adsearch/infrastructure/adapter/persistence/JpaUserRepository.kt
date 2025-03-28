package com.adsearch.infrastructure.adapter.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.repository.UserRepositoryPort
import com.adsearch.infrastructure.repository.entity.UserEntity
import com.adsearch.infrastructure.repository.jpa.UserJpaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

/**
 * Adapter implementation of UserRepositoryPort using JPA
 */
@Repository
@Primary
class JpaUserRepository(
    private val userJpaRepository: UserJpaRepository
) : UserRepositoryPort {
    
    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val entity = userJpaRepository.save(UserEntity.fromDomain(user))
        entity.toDomain()
    }
    
    override suspend fun findById(id: Long): User? = withContext(Dispatchers.IO) {
        userJpaRepository.findById(id).orElse(null)?.toDomain()
    }
    
    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userJpaRepository.findByUsername(username)?.toDomain()
    }
    
    override suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        userJpaRepository.findAll().map { it.toDomain() }
    }
}
