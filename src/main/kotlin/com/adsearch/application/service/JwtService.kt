package com.adsearch.application.service

import com.adsearch.domain.model.User
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

/**
 * Service for JWT token operations
 */
@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String,
    
    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long,
    
    @Value("\${jwt.issuer}")
    private val issuer: String
) {
    
    /**
     * Generate a JWT token for a user
     */
    fun generateToken(user: User): String {
        val algorithm = Algorithm.HMAC256(secret)
        
        return JWT.create()
            .withSubject(user.id.toString())
            .withClaim("username", user.username)
            .withArrayClaim("roles", user.roles.toTypedArray())
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiration))
            .withIssuer(issuer)
            .sign(algorithm)
    }
    
    /**
     * Validate a JWT token
     */
    fun validateToken(token: String): Boolean {
        return try {
            val algorithm = Algorithm.HMAC256(secret)
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build()
            
            verifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }
    
    /**
     * Extract user ID from a JWT token
     */
    fun getUserIdFromToken(token: String): UUID {
        val algorithm = Algorithm.HMAC256(secret)
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
        
        val jwt = verifier.verify(token)
        return UUID.fromString(jwt.subject)
    }
    
    /**
     * Extract username from a JWT token
     */
    fun getUsernameFromToken(token: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
        
        val jwt = verifier.verify(token)
        return jwt.getClaim("username").asString()
    }
    
    /**
     * Extract roles from a JWT token
     */
    fun getRolesFromToken(token: String): List<String> {
        val algorithm = Algorithm.HMAC256(secret)
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
        
        val jwt = verifier.verify(token)
        return jwt.getClaim("roles").asList(String::class.java)
    }
    
    /**
     * Decode a JWT token
     */
    fun decodeToken(token: String): DecodedJWT {
        val algorithm = Algorithm.HMAC256(secret)
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .build()
        
        return verifier.verify(token)
    }
}
