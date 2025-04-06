package com.adsearch.infrastructure.security.service

import com.adsearch.infrastructure.security.model.JwtUserDetails
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

/**
 * Service for JWT token operations
 */
@Service
class JwtAccessTokenService(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long,

    @Value("\${jwt.issuer}")
    private val issuer: String
) {

    private final val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private final val verifier: JWTVerifier = JWT.require(this.algorithm).withIssuer(issuer).build()

    /**
     * Generate a JWT token for a user
     */
    fun generateAccessToken(user: JwtUserDetails): String {
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
     * Validate a JWT token
     */
    fun validateTokenAndGetUsername(token: String): String? = try {
        verifier.verify(token).subject
    } catch (verificationEx: JWTVerificationException) {
        null
    }
}
