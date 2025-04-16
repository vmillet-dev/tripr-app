package com.adsearch.infrastructure.security.service

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.TokenServicePort
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import java.util.UUID

/**
 * Implementation of TokenServicePort
 */
@Service
class TokenServiceImpl(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long,
    @Value("\${jwt.issuer}") private val issuer: String,
    @Value("\${password-reset.token-expiration}") private val passwordResetTokenExpiration: Long,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort
) : TokenServicePort {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(TokenServiceImpl::class.java)
    }

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    override fun generateAccessToken(userId: Long, username: String, roles: List<String>): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("username", username)
            .withArrayClaim("roles", roles.toTypedArray())
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiration))
            .withIssuer(issuer)
            .sign(algorithm)
    }

    override fun generateRefreshToken(userId: Long): RefreshToken {
        LOG.debug("Creating refresh token for user ID: $userId")

        // Delete any existing tokens for this user
        refreshTokenPersistencePort.deleteByUserId(userId)

        return RefreshToken(
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration)
        )
    }

    override fun generatePasswordResetToken(userId: Long): PasswordResetToken {
        return PasswordResetToken(
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(passwordResetTokenExpiration)
        )
    }

    override fun validateAccessToken(token: String): String? = try {
        verifier.verify(token).subject
    } catch (verificationEx: JWTVerificationException) {
        null
    }

    override fun validateRefreshToken(token: String): Long {
        val refreshToken: RefreshToken? = refreshTokenPersistencePort.findByToken(token)

        if (refreshToken == null) {
            LOG.warn("Refresh token invalid")
            throw InvalidTokenException()
        }
        
        if (isTokenExpired(refreshToken.expiryDate) || refreshToken.revoked) {
            refreshTokenPersistencePort.deleteById(refreshToken.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
        
        return refreshToken.userId
    }

    override fun isTokenExpired(expiryDate: Instant): Boolean {
        return expiryDate.isBefore(Instant.now())
    }
}
