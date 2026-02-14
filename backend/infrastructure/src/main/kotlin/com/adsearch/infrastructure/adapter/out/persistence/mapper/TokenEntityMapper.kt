package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.TokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.TokenEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * Common mapper for TokenEntity and domain models
 */
@Mapper(componentModel = "spring")
interface TokenEntityMapper {
    @Mapping(target = "token", source = "token")
    fun toPasswordResetDomain(entity: TokenEntity): PasswordResetTokenDom

    @Mapping(target = "token", source = "token")
    fun toRefreshDomain(entity: TokenEntity): RefreshTokenDom

    @Mapping(target = "id", ignore = true)
    fun toEntity(domain: TokenDom): TokenEntity
}
