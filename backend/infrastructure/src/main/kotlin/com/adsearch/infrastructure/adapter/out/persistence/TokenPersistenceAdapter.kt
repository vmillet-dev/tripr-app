package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.model.Token
import com.adsearch.domain.model.User
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.TokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.TokenEntityMapper
import org.springframework.stereotype.Component

/**
 * Persistence adapter for tokens
 */
@Component
class TokenPersistenceAdapter(
    private val tokenRepository: TokenRepository,
    private val tokenEntityMapper: TokenEntityMapper
) : TokenPersistencePort {

    override fun save(dom: Token) {
        tokenRepository.save(tokenEntityMapper.toEntity(dom))
    }

    override fun findByToken(token: String, type: TokenTypeEnum): Token? {
        val entity = tokenRepository.findByTokenAndType(token, type) ?: return null
        return tokenEntityMapper.toDomain(entity)
    }

    override fun delete(dom: Token) {
        tokenRepository.deleteByTokenAndType(dom.token, dom.type)
    }

    override fun deleteRefreshTokenByUser(user: User) {
        tokenRepository.deleteByUserIdAndType(user.id, TokenTypeEnum.REFRESH)
    }

    override fun deletePasswordRestTokenByUser(user: User) {
        tokenRepository.deleteByUserIdAndType(user.id, TokenTypeEnum.PASSWORD_RESET)
    }
}
