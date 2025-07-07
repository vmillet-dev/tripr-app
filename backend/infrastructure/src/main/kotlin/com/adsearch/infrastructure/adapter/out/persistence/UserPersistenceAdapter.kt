package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.out.UserPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper,
) : UserPersistencePort {

    override fun save(userDom: UserDom) {
        userRepository.save(userEntityMapper.toEntity(userDom))
    }

    override fun findByUsername(username: String): UserDom? {
        return userRepository.findByUsername(username)?.let { userEntityMapper.toDomain(it) }
    }

    override fun findById(id: Long): UserDom? {
        return userRepository.findById(id).orElse(null)?.let { userEntityMapper.toDomain(it) }
    }

    override fun findByEmail(email: String): UserDom? {
        return userRepository.findByEmail(email)?.let { userEntityMapper.toDomain(it) }
    }
}
