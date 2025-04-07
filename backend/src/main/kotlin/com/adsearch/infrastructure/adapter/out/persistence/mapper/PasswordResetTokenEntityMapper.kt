package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between PasswordResetToken domain model and PasswordResetTokenEntity
 */
@Component
class PasswordResetTokenEntityMapper : EntityMapper<PasswordResetTokenEntity, PasswordResetToken> {
    
    override fun toEntity(domainModel: PasswordResetToken): PasswordResetTokenEntity {
        return PasswordResetTokenEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            token = domainModel.token,
            expiryDate = domainModel.expiryDate,
            used = domainModel.used
        )
    }
    
    override fun toDomain(entity: PasswordResetTokenEntity): PasswordResetToken {
        return PasswordResetToken(
            id = entity.id,
            userId = entity.userId,
            token = entity.token,
            expiryDate = entity.expiryDate,
            used = entity.used
        )
    }
}
