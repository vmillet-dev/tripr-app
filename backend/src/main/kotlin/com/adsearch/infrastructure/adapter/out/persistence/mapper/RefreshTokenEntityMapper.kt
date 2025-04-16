package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for RefreshToken entity and domain model
 */
@Component
class RefreshTokenEntityMapper : EntityMapper<RefreshTokenEntity, RefreshToken> {
    
    override fun toDomain(entity: RefreshTokenEntity): RefreshToken = RefreshToken(
        id = entity.id,
        userId = entity.userId,
        token = entity.token,
        expiryDate = entity.expiryDate,
        revoked = entity.revoked
    )
    
    override fun fromDomain(domain: RefreshToken): RefreshTokenEntity = RefreshTokenEntity(
        id = domain.id,
        userId = domain.userId,
        token = domain.token,
        expiryDate = domain.expiryDate,
        revoked = domain.revoked
    )
}
