package com.adsearch.infrastructure.security.service

import com.adsearch.common.exception.InvalidTokenException
import com.adsearch.common.exception.TokenExpiredException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.domain.port.UserPersistencePort
import com.adsearch.infrastructure.security.model.JwtUserDetails
import com.adsearch.infrastructure.security.port.JwtTokenServicePort
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

/**
 * Service for JWT token operations
 */
@Service
class JwtTokenService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val jwtExpiration: Long,
    @Value("\${jwt.refresh-token.expiration}") private val refreshTokenExpiration: Long,
    @Value("\${jwt.issuer}") private val issuer: String,

    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val userPersistencePort: UserPersistencePort
) : JwtTokenServicePort {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private final val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private final val verifier: JWTVerifier = JWT.require(this.algorithm).withIssuer(issuer).build()

    /**
     * Generate a JWT token for a user
     */
    override fun createAccessToken(user: JwtUserDetails): String {
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
    override suspend fun createRefreshToken(user: JwtUserDetails): RefreshToken {
        LOG.debug("Creating refresh token for user: ${user.username} with ID: ${user.id}")

        // Delete any existing tokens for this user
        refreshTokenPersistencePort.deleteByUserId(user.id)

        val refreshToken = RefreshToken(
            userId = user.id,
            token = java.util.UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenExpiration)
        )

        val savedToken = refreshTokenPersistencePort.save(refreshToken)
        LOG.debug("Saved refresh token: {}", savedToken)

        return savedToken
    }

    /**
     * Validate a JWT token
     */
    override fun validateAccessTokenAndGetUsername(token: String): String? = try {
        verifier.verify(token).subject
    } catch (verificationEx: JWTVerificationException) {
        null
    }

    override suspend fun validateRefreshTokenAndGetUsername(givenToken: String): String {
        val refreshToken: RefreshToken? = refreshTokenPersistencePort.findByToken(givenToken)

        if (refreshToken == null) {
            LOG.warn("refresh token invalid")
            throw InvalidTokenException()
        }
        
        verifyExpiration(refreshToken)
        return userPersistencePort.findById(refreshToken.userId)!!.username
    }

    /**
     * Verify if a refresh token is valid
     */
    private suspend fun verifyExpiration(token: RefreshToken) {
        if (token.expiryDate.isBefore(Instant.now()) || token.revoked) {
            refreshTokenPersistencePort.deleteById(token.id)
            throw TokenExpiredException("Refresh token was expired. Please make a new sign in request")
        }
    }
}
