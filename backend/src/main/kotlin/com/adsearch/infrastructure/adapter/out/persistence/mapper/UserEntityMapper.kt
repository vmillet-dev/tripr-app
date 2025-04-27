package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for User entity and domain model
 */
@Konverter
@KComponent
interface UserEntityMapper {
    fun toDomain(entity: UserEntity): UserDom
    fun fromDomain(domain: UserDom): UserEntity
}
