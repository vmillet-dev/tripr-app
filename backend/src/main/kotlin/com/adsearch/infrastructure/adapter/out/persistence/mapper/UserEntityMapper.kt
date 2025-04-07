package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.common.mapper.mapTo
import com.adsearch.common.mapper.mapToList
import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between User domain model and UserEntity
 * using dynamic mapper
 */
@Component
class UserEntityMapper {
    
    fun toEntity(domainModel: User): UserEntity {
        return domainModel.mapTo<UserEntity>()
    }
    
    fun toDomain(entity: UserEntity): User {
        return entity.mapTo<User>()
    }
    
    fun toEntityList(domainModels: List<User>): List<UserEntity> {
        return domainModels.mapToList<UserEntity>()
    }
    
    fun toDomainList(entities: List<UserEntity>): List<User> {
        return entities.mapToList<User>()
    }
}
