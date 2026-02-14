package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.TokenDom
import com.adsearch.domain.model.enums.TokenTypeEnum
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

    override fun save(dom: TokenDom) {
        tokenRepository.save(tokenEntityMapper.toEntity(dom))
    }

    override fun findByToken(token: String, type: TokenTypeEnum): TokenDom? {
        val entity = tokenRepository.findByTokenAndType(token, type) ?: return null
        return when (type) {
            TokenTypeEnum.PASSWORD_RESET -> tokenEntityMapper.toPasswordResetDomain(entity)
            TokenTypeEnum.REFRESH -> tokenEntityMapper.toRefreshDomain(entity)
        }
    }

    override fun deleteByToken(token: String, type: TokenTypeEnum) {
        tokenRepository.deleteByTokenAndType(token, type)
    }

    override fun deleteByUserId(userId: Long, type: TokenTypeEnum) {
        tokenRepository.deleteByUserIdAndType(userId, type)
    }
}
