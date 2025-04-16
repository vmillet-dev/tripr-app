package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(private val userRepository: UserRepository) : UserPersistencePort {

    override fun save(user: User): User {
        val entity = userRepository.save(UserEntity.fromDomain(user))
        return entity.toDomain()
    }

    override fun findById(id: Long): User? {
        return userRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)?.toDomain()
    }

    override fun findAll(): List<User> {
        return userRepository.findAll().map { it.toDomain() }
    }
}
