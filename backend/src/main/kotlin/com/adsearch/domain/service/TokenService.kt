package com.adsearch.domain.service

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import java.time.Instant
import java.util.UUID

/**
 * Domain service for token operations
 */
class TokenService {

    /**
     * Create a refresh token for a user
     */
    fun createRefreshToken(userId: Long, expirationMillis: Long): RefreshToken {
        return RefreshToken(
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(expirationMillis),
            revoked = false
        )
    }

    /**
     * Verify if a refresh token is valid
     */
    fun verifyExpiration(token: RefreshToken) {
        if (token.expiryDate.isBefore(Instant.now()) || token.revoked) {
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
    }
}
