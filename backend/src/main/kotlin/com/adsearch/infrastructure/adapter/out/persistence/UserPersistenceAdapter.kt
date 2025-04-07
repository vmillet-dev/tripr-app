package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper
) : UserPersistencePort {

    override suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val entity = userRepository.save(userEntityMapper.toEntity(user))
        userEntityMapper.toDomain(entity)
    }

    override suspend fun findById(id: Long): User? = withContext(Dispatchers.IO) {
        userRepository.findById(id).orElse(null)?.let { userEntityMapper.toDomain(it) }
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)?.let { userEntityMapper.toDomain(it) }
    }

    override suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        userEntityMapper.toDomainList(userRepository.findAll())
    }
}
