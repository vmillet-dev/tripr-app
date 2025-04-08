package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between User domain model and UserEntity
 */
@Component
class UserEntityMapper : EntityMapper<UserEntity, User> {
    
    override fun toEntity(domainModel: User): UserEntity {
        return UserEntity(
            id = domainModel.id,
            username = domainModel.username,
            password = domainModel.password,
            roles = domainModel.roles.toMutableList()
        )
    }
    
    override fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            password = entity.password,
            roles = entity.roles
        )
    }
}
