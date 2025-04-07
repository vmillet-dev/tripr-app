package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Mapper for converting between RefreshToken domain model and RefreshTokenEntity
 */
@Component
class RefreshTokenEntityMapper {
    
    fun toEntity(domainModel: RefreshToken): RefreshTokenEntity {
        return RefreshTokenEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            token = domainModel.token,
            expiryDate = domainModel.expiryDate,
            revoked = domainModel.revoked
        )
    }
    
    fun toDomain(entity: RefreshTokenEntity): RefreshToken {
        return RefreshToken(
            id = entity.id,
            userId = entity.userId,
            token = entity.token,
            expiryDate = entity.expiryDate,
            revoked = entity.revoked
        )
    }
    
    fun toEntityList(domainModels: List<RefreshToken>): List<RefreshTokenEntity> {
        return domainModels.map { toEntity(it) }
    }
    
    fun toDomainList(entities: List<RefreshTokenEntity>): List<RefreshToken> {
        return entities.map { toDomain(it) }
    }
}
