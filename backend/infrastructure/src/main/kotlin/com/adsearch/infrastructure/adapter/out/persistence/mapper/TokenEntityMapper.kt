package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.TokenDom
import com.adsearch.domain.model.enums.TokenTypeEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.TokenEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * Common mapper for TokenEntity and domain models
 */
@Mapper(componentModel = "spring")
interface TokenEntityMapper {
    fun toPasswordResetDomain(entity: TokenEntity): PasswordResetTokenDom
    fun toRefreshDomain(entity: TokenEntity): RefreshTokenDom

    @Mapping(target = "id", ignore = true)
    fun toEntity(domain: TokenDom): TokenEntity

    /**
     * Maps TokenEntity to its specific domain implementation
     */
    fun toDomain(entity: TokenEntity): TokenDom {
        return if (entity.type == TokenTypeEnum.REFRESH) {
            toRefreshDomain(entity)
        } else {
            toPasswordResetDomain(entity)
        }
    }
}
