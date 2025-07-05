package com.adsearch.domain.port.out

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.UserDom

/**
 * Port for password reset token repository operations
 */
interface PasswordResetTokenPersistencePort {

    /**
     * Find a token by token string
     */
    fun findByToken(token: String): PasswordResetTokenDom?

    /**
     * Save a token
     */
    fun save(dom: PasswordResetTokenDom)

    /**
     * Delete a token by ID
     */
    fun deleteByToken(token: String)

    /**
     * Delete all tokens for a user
     */
    fun deleteByUser(user: UserDom)
}
