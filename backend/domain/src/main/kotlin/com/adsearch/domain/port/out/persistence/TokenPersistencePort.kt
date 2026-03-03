package com.adsearch.domain.port.out.persistence

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.model.Token
import com.adsearch.domain.model.User

/**
 * Port for token repository operations independent of token subtype
 */
interface TokenPersistencePort {
    fun findByTokenAndType(token: String, type: TokenTypeEnum): Token?
    fun save(dom: Token)
    fun deleteTokenAndType(token: String, type: TokenTypeEnum)
    fun deleteByUserAndType(user: User, type: TokenTypeEnum)
}

fun TokenPersistencePort.findRefreshTokenByToken(token: String) = findByTokenAndType(token, TokenTypeEnum.REFRESH)
fun TokenPersistencePort.deleteRefreshTokenByToken(token: String) = deleteTokenAndType(token, TokenTypeEnum.REFRESH)

fun TokenPersistencePort.findPasswordResetTokenByToken(token: String) = findByTokenAndType(token, TokenTypeEnum.PASSWORD_RESET)
fun TokenPersistencePort.deletePasswordResetTokenByToken(token: String) = deleteTokenAndType(token, TokenTypeEnum.PASSWORD_RESET)
fun TokenPersistencePort.deletePasswordResetTokenByUser(user: User) = deleteByUserAndType(user, TokenTypeEnum.PASSWORD_RESET)
