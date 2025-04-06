package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(private val userRepository: UserRepository) : UserPersistencePort {

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val entity = userRepository.save(UserEntity.fromDomain(user))
        entity.toDomain()
    }

    override suspend fun findById(id: Long): User? = withContext(Dispatchers.IO) {
        userRepository.findById(id).orElse(null)?.toDomain()
    }

    override suspend fun findByUsername(username: String): User? = withContext(Dispatchers.IO) {
        userRepository.findByUsername(username)?.toDomain()
    }

    override suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        userRepository.findAll().map { it.toDomain() }
    }
}
