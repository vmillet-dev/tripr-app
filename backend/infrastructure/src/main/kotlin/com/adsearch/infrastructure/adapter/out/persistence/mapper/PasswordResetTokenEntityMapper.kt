package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.mapstruct.Mapper

/**
 * Mapper for PasswordResetTokenEntity entity and domain model
 */
@Mapper(componentModel = "spring", uses = [RoleEntityMapper::class])
interface PasswordResetTokenEntityMapper {
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetTokenDom
    fun fromDomain(domain: PasswordResetTokenDom): PasswordResetTokenEntity
}
