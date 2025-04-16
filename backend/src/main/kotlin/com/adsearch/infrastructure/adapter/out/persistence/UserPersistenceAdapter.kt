package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper
) : UserPersistencePort {

    override fun save(user: User): User = 
        userRepository.save(userEntityMapper.toEntity(user)).let(userEntityMapper::toDomain)

    override fun findById(id: Long): User? =
        userRepository.findById(id).orElse(null)?.let(userEntityMapper::toDomain)

    override fun findByUsername(username: String): User? =
        userRepository.findByUsername(username)?.let(userEntityMapper::toDomain)

    override fun findAll(): List<User> =
        userRepository.findAll().map(userEntityMapper::toDomain)
}
