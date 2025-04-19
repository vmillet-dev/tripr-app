package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for PasswordResetToken entity and domain model
 */
@Konverter
@KComponent
interface PasswordResetTokenEntityMapper {
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetToken
    fun fromDomain(domain: PasswordResetToken): PasswordResetTokenEntity
}
