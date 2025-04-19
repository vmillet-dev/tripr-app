package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.Role
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for Role entity and domain model
 */
@Konverter
@KComponent
interface RoleEntityMapper {
    fun toDomain(entity: RoleEntity): Role
    fun fromDomain(domain: Role): RoleEntity
}
