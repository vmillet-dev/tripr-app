package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.Token
import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.TokenEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

/**
 * Common mapper for TokenEntity and domain models
 */
@Mapper(componentModel = "spring")
interface TokenEntityMapper {
    fun toPasswordResetDomain(entity: TokenEntity): PasswordResetToken
    fun toRefreshDomain(entity: TokenEntity): RefreshToken

    @Mapping(target = "id", ignore = true)
    fun toEntity(domain: Token): TokenEntity

    /**
     * Maps TokenEntity to its specific domain implementation
     */
    fun toDomain(entity: TokenEntity): Token {
        return if (entity.type == TokenTypeEnum.REFRESH) {
            toRefreshDomain(entity)
        } else {
            toPasswordResetDomain(entity)
        }
    }
}
