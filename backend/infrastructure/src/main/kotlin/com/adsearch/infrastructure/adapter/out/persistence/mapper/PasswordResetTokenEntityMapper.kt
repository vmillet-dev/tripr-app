package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for PasswordResetToken entity and domain model
 */
@Konverter
@KComponent
interface PasswordResetTokenEntityMapper {
    fun toDomain(entity: PasswordResetTokenEntity): PasswordResetTokenDom
    fun fromDomain(domain: PasswordResetTokenDom): PasswordResetTokenEntity
}
