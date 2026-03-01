package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper,
) : UserPersistencePort {

    override fun save(user: User) {
        userRepository.save(userEntityMapper.toEntity(user))
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)?.let { userEntityMapper.toDomain(it) }
    }

    override fun findById(id: Long): User? {
        return userEntityMapper.toDomain(userRepository.findById(id).orElse(null))
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)?.let { userEntityMapper.toDomain(it) }
    }
}
