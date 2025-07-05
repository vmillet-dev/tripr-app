package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.UserDom
import com.adsearch.domain.model.enum.UserRoleEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

/**
 * Mapper for User entity and domain model
 */
@Mapper(componentModel = "spring")
interface UserEntityMapper {
    @Mapping(target = "roles", qualifiedByName = ["map"])
    fun toDomain(entity: UserEntity): UserDom
    fun fromDomain(domain: UserDom): UserEntity

    fun map(value: String): RoleEntity {
        val roleEnum = UserRoleEnum.valueOf(value)
        return RoleEntity(roleEnum.id, roleEnum.type)
    }

    @Named("map")
    fun map(value: RoleEntity): String {
        return value.type
    }
}
