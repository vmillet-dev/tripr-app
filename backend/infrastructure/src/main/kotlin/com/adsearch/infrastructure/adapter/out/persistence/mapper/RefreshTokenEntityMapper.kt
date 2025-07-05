package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.mapstruct.Mapper

/**
 * Mapper for RefreshTokenEntity entity and domain model
 */
@Mapper(componentModel = "spring", uses = [RoleEntityMapper::class])
interface RefreshTokenEntityMapper {
    fun toDomain(entity: RefreshTokenEntity): RefreshTokenDom
    fun toEntity(domain: RefreshTokenDom): RefreshTokenEntity
}
