package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.mapstruct.Mapper

/**
 * Mapper for PasswordResetToken entity and domain model
 */
@Mapper(componentModel = "spring")
interface PasswordResetTokenEntityMapper {
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetTokenDom
    fun fromDomain(domain: PasswordResetTokenDom): PasswordResetTokenEntity
}
