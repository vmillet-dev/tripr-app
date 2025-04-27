package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.enum.UserRoleEnum
import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for User entity and domain model
 */
@Konverter
@KComponent
interface UserEntityMapper {
    @Konvert(mappings=[Mapping(target = "roles", expression = "it.roles.map { it.type }")])
    fun toDomain(entity: UserEntity): UserDom
    @Konvert(mappings=[Mapping(target = "roles", expression = "rolesToEntities(it)")])
    fun fromDomain(domain: UserDom): UserEntity

    fun rolesToEntities(domain: UserDom): MutableSet<RoleEntity>  {
        return domain.roles
            .map { it -> UserRoleEnum.valueOf(it) }
            .map { it -> RoleEntity(it.id, it.type) }
            .toMutableSet()
    }
}
