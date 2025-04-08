package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between RefreshToken domain model and RefreshTokenEntity
 */
@Component
class RefreshTokenEntityMapper : EntityMapper<RefreshTokenEntity, RefreshToken> {
    
    override fun toEntity(domainModel: RefreshToken): RefreshTokenEntity {
        return RefreshTokenEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            token = domainModel.token,
            expiryDate = domainModel.expiryDate,
            revoked = domainModel.revoked
        )
    }
    
    override fun toDomain(entity: RefreshTokenEntity): RefreshToken {
        return RefreshToken(
            id = entity.id,
            userId = entity.userId,
            token = entity.token,
            expiryDate = entity.expiryDate,
            revoked = entity.revoked
        )
    }
}
