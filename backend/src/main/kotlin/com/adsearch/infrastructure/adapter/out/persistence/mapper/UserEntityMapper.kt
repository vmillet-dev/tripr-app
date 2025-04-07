package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between User domain model and UserEntity
 */
@Component
class UserEntityMapper {
    
    fun toEntity(domainModel: User): UserEntity {
        return UserEntity.fromDomain(domainModel)
    }
    
    fun toDomain(entity: UserEntity): User {
        return entity.toDomain()
    }
    
    fun toEntityList(domainModels: List<User>): List<UserEntity> {
        return domainModels.map { toEntity(it) }
    }
    
    fun toDomainList(entities: List<UserEntity>): List<User> {
        return entities.map { toDomain(it) }
    }
}
