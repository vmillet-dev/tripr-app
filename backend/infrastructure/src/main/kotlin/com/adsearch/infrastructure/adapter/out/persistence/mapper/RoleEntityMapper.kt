package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.enums.UserRoleEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface RoleEntityMapper {
    fun toEntity(value: String): RoleEntity {
        val roleEnum = UserRoleEnum.valueOf(value)
        return RoleEntity(roleEnum.id, roleEnum.type)
    }

    fun fromEntity(value: RoleEntity): String {
        return value.type
    }
}
