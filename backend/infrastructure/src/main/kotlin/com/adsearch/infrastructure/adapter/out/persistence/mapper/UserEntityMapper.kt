package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

/**
 * Mapper for UserEntity entity and domain model
 */
@Mapper(componentModel = "spring", uses = [RoleEntityMapper::class], unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserEntityMapper {
    fun toDomain(entity: UserEntity): User
    fun toEntity(domain: User): UserEntity
}
