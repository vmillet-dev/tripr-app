package com.adsearch.infrastructure.security.service

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.security.model.JwtUserDetails
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
 * Service for JWT token operations
 */
@Service
class JwtTokenService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long,
    @Value("\${jwt.issuer}") private val issuer: String,
    @Value("\${password-reset.token-expiration}") private val passwordResetTokenExpiration: Long,
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val userPersistencePort: UserPersistencePort
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(JwtTokenService::class.java)
    }

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    /**
     * Generate a JWT token for a user
     */
    fun createAccessToken(user: JwtUserDetails): String {
        return JWT.create()
            .withSubject(user.id.toString())
            .withClaim("username", user.username)
            .withArrayClaim("roles", user.authorities.map { it.authority }.toTypedArray())
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiration))
            .withIssuer(issuer)
            .sign(algorithm)
    }

    /**
     * Create a refresh token for a user
     */
    fun createRefreshToken(user: JwtUserDetails): RefreshToken {
        LOG.debug("Creating refresh token for user: ${user.username} with ID: ${user.id}")

        // Delete any existing tokens for this user
        refreshTokenPersistencePort.deleteByUserId(user.id)

        val refreshToken = RefreshToken(
            userId = user.id,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration)
        )

        val savedToken = refreshTokenPersistencePort.save(refreshToken)
        LOG.debug("Saved refresh token: {}", savedToken)

        return savedToken
    }

    /**
     * Generate a password reset token for a user
     */
    fun generatePasswordResetToken(userId: Long): PasswordResetToken {
        return PasswordResetToken(
            userId = userId,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(passwordResetTokenExpiration)
        )
    }

    /**
     * Validate a JWT token
     */
    fun validateAccessTokenAndGetUsername(token: String): String? = try {
        verifier.verify(token).subject
    } catch (verificationEx: JWTVerificationException) {
        null
    }

    /**
     * Validate a refresh token and get the username
     */
    fun validateRefreshTokenAndGetUsername(givenToken: String): String {
        val refreshToken: RefreshToken? = refreshTokenPersistencePort.findByToken(givenToken)

        if (refreshToken == null) {
            LOG.warn("Refresh token invalid")
            throw InvalidTokenException()
        }
        
        if (isTokenExpired(refreshToken.expiryDate) || refreshToken.revoked) {
            refreshTokenPersistencePort.deleteById(refreshToken.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
        
        return userPersistencePort.findById(refreshToken.userId)?.username
            ?: throw RuntimeException("User not found for refresh token")
    }

    /**
     * Check if a token is expired
     */
    fun isTokenExpired(expiryDate: Instant): Boolean {
        return expiryDate.isBefore(Instant.now())
    }
}
