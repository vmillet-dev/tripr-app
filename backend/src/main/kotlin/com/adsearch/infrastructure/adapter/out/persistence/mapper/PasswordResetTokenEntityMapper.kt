package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.common.mapper.mapTo
import com.adsearch.common.mapper.mapToList
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between PasswordResetToken domain model and PasswordResetTokenEntity
 * using dynamic mapper
 */
@Component
class PasswordResetTokenEntityMapper {
    
    fun toEntity(domainModel: PasswordResetToken): PasswordResetTokenEntity {
        return domainModel.mapTo<PasswordResetTokenEntity>()
    }
    
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetToken {
        return entity.mapTo<PasswordResetToken>()
    }
    
    fun toEntityList(domainModels: List<PasswordResetToken>): List<PasswordResetTokenEntity> {
        return domainModels.mapToList<PasswordResetTokenEntity>()
    }
    
    fun toDomainList(entities: List<PasswordResetTokenEntity>): List<PasswordResetToken> {
        return entities.mapToList<PasswordResetToken>()
    }
}
