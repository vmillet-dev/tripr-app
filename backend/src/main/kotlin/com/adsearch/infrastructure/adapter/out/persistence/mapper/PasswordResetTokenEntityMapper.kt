package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between PasswordResetToken domain model and PasswordResetTokenEntity
 * 
 * Note: Uses the entity's built-in toDomain() and fromDomain() methods
 * to maintain compatibility with existing code
 */
@Component
class PasswordResetTokenEntityMapper {
    
    fun toEntity(domainModel: PasswordResetToken): PasswordResetTokenEntity {
        return PasswordResetTokenEntity.fromDomain(domainModel)
    }
    
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetToken {
        return entity.toDomain()
    }
    
    fun toEntityList(domainModels: List<PasswordResetToken>): List<PasswordResetTokenEntity> {
        return domainModels.map { toEntity(it) }
    }
    
    fun toDomainList(entities: List<PasswordResetTokenEntity>): List<PasswordResetToken> {
        return entities.map { toDomain(it) }
    }
}
