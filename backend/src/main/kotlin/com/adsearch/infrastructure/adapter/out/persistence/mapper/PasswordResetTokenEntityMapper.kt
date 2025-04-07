package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Mapper for converting between PasswordResetToken domain model and PasswordResetTokenEntity
 */
@Component
class PasswordResetTokenEntityMapper {
    
    fun toEntity(domainModel: PasswordResetToken): PasswordResetTokenEntity {
        return PasswordResetTokenEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            token = domainModel.token,
            expiryDate = domainModel.expiryDate
        )
    }
    
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetToken {
        return PasswordResetToken(
            id = entity.id,
            userId = entity.userId,
            token = entity.token,
            expiryDate = entity.expiryDate
        )
    }
    
    fun toEntityList(domainModels: List<PasswordResetToken>): List<PasswordResetTokenEntity> {
        return domainModels.map { toEntity(it) }
    }
    
    fun toDomainList(entities: List<PasswordResetTokenEntity>): List<PasswordResetToken> {
        return entities.map { toDomain(it) }
    }
}
