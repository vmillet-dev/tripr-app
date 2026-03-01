package com.adsearch.domain.port.out.persistence

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.model.Token
import com.adsearch.domain.model.User

/**
 * Port for token repository operations independent of token subtype
 */
interface TokenPersistencePort {
    fun findByToken(token: String, type: TokenTypeEnum): Token?
    fun save(dom: Token)
    fun delete(dom: Token)
    fun deleteRefreshTokenByUser(user: User)
    fun deletePasswordRestTokenByUser(user: User)
}
