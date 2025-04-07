package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.common.mapper.mapTo
import com.adsearch.common.mapper.mapToList
import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between RefreshToken domain model and RefreshTokenEntity
 * using dynamic mapper
 */
@Component
class RefreshTokenEntityMapper {
    
    fun toEntity(domainModel: RefreshToken): RefreshTokenEntity {
        return domainModel.mapTo<RefreshTokenEntity>()
    }
    
    fun toDomain(entity: RefreshTokenEntity): RefreshToken {
        return entity.mapTo<RefreshToken>()
    }
    
    fun toEntityList(domainModels: List<RefreshToken>): List<RefreshTokenEntity> {
        return domainModels.mapToList<RefreshTokenEntity>()
    }
    
    fun toDomainList(entities: List<RefreshTokenEntity>): List<RefreshToken> {
        return entities.mapToList<RefreshToken>()
    }
}
