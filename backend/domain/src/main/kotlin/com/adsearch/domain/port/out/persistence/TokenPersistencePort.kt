package com.adsearch.domain.port.out.persistence

import com.adsearch.domain.model.TokenDom
import com.adsearch.domain.model.enums.TokenTypeEnum

/**
 * Port for token repository operations independent of token subtype
 */
interface TokenPersistencePort {
    fun findByToken(token: String, type: TokenTypeEnum): TokenDom?
    fun save(dom: TokenDom)
    fun delete(dom: TokenDom)
    fun deleteByUserId(userId: Long, type: TokenTypeEnum)
}
