package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.stereotype.Component

/**
 * Mapper for User entity and domain model
 */
@Component
class UserEntityMapper : EntityMapper<UserEntity, User> {
    
    override fun toDomain(entity: UserEntity): User = User(
        id = entity.id,
        username = entity.username,
        password = entity.password,
        roles = entity.roles.toList()
    )
    
    override fun fromDomain(domain: User): UserEntity = UserEntity(
        id = domain.id,
        username = domain.username,
        password = domain.password,
        roles = domain.roles.toMutableList()
    )
}
