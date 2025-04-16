package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(private val userRepository: UserRepository) : UserPersistencePort {

    override fun save(user: User): User = 
        userRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun findById(id: Long): User? =
        userRepository.findById(id).orElse(null)?.toDomain()

    override fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)?.toDomain()

    override fun findAll(): List<User> =
        userRepository.findAll().map { it.toDomain() }
}
