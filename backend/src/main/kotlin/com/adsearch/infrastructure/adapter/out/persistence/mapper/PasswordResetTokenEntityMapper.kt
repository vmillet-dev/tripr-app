package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for PasswordResetToken entity and domain model
 */
@Component
class PasswordResetTokenEntityMapper : EntityMapper<PasswordResetTokenEntity, PasswordResetToken> {
    
    override fun toDomain(entity: PasswordResetTokenEntity): PasswordResetToken = PasswordResetToken(
        id = entity.id,
        userId = entity.userId,
        token = entity.token,
        expiryDate = entity.expiryDate,
        used = entity.used
    )
    
    override fun fromDomain(domain: PasswordResetToken): PasswordResetTokenEntity = PasswordResetTokenEntity(
        id = domain.id,
        userId = domain.userId,
        token = domain.token,
        expiryDate = domain.expiryDate,
        used = domain.used
    )
}
