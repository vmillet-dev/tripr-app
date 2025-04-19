package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for RefreshToken entity and domain model
 */
@Konverter
@KComponent
interface RefreshTokenEntityMapper {
    fun toDomain(entity: RefreshTokenEntity): RefreshToken
    fun fromDomain(domain: RefreshToken): RefreshTokenEntity
}
