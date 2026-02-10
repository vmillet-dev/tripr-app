package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.out.security.TokenGeneratorPort
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.JWTVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtTokenGeneratorAdapter(
    @param:Value("\${jwt.secret}") private val secret: String,
    @param:Value("\${jwt.expiration}") private val jwtExpiration: Long,
    @param:Value("\${jwt.issuer}") private val issuer: String
) : TokenGeneratorPort {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    override fun generateAccessToken(user: UserDom): String = JWT.create()
        .withSubject(user.username)
        .withArrayClaim("roles", user.roles.toTypedArray())
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
        .withIssuer(issuer)
        .sign(algorithm)

    override fun validateAccessTokenAndGetUsername(token: String): String? = try {
        verifier.verify(token).subject
    } catch (_: JWTVerificationException) {
        null
    }
}
