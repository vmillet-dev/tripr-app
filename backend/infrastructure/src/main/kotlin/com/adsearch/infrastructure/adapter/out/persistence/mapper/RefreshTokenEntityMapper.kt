package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.mapstruct.Mapper

/**
 * Mapper for RefreshToken entity and domain model
 */
@Mapper(componentModel = "spring")
interface RefreshTokenEntityMapper {
    fun toDomain(entity: RefreshTokenEntity): RefreshTokenDom
    fun fromDomain(domain: RefreshTokenDom): RefreshTokenEntity
}
