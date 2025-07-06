package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * Mapper for RefreshTokenEntity entity and domain model
 */
@Mapper(componentModel = "spring", uses = [RoleEntityMapper::class])
interface RefreshTokenEntityMapper {
    fun toDomain(entity: RefreshTokenEntity): RefreshTokenDom

    @Mapping(target = "id", ignore = true)
    fun toEntity(domain: RefreshTokenDom): RefreshTokenEntity
}
