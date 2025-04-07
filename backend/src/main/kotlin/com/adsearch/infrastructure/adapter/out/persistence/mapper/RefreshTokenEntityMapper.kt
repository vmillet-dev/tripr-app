package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between RefreshToken domain model and RefreshTokenEntity
 * 
 * Note: Uses the entity's built-in toDomain() and fromDomain() methods
 * to maintain compatibility with existing code
 */
@Component
class RefreshTokenEntityMapper {
    
    fun toEntity(domainModel: RefreshToken): RefreshTokenEntity {
        return RefreshTokenEntity.fromDomain(domainModel)
    }
    
    fun toDomain(entity: RefreshTokenEntity): RefreshToken {
        return entity.toDomain()
    }
    
    fun toEntityList(domainModels: List<RefreshToken>): List<RefreshTokenEntity> {
        return domainModels.map { toEntity(it) }
    }
    
    fun toDomainList(entities: List<RefreshTokenEntity>): List<RefreshToken> {
        return entities.map { toDomain(it) }
    }
}
