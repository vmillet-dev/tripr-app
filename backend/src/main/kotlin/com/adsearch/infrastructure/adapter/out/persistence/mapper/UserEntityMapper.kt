package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for User entity and domain model
 */
@Konverter
@KComponent
interface UserEntityMapper {
    fun toDomain(entity: UserEntity): User
    fun fromDomain(domain: User): UserEntity
}
