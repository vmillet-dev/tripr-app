package com.adsearch.infrastructure.repository.entity

import com.adsearch.domain.model.RefreshToken
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val userId: Long,
    
    @Column(nullable = false, unique = true)
    val token: String,
    
    @Column(nullable = false)
    val expiryDate: Instant,
    
    @Column(nullable = false)
    val revoked: Boolean = false
) {
    // Default constructor required by JPA
    constructor() : this(
        id = 0,
        userId = 0,
        token = "",
        expiryDate = Instant.now(),
        revoked = false
    )
    
    fun toDomain(): RefreshToken {
        return RefreshToken(
            id = id,
            userId = userId,
            token = token,
            expiryDate = expiryDate,
            revoked = revoked
        )
    }
    
    companion object {
        fun fromDomain(refreshToken: RefreshToken): RefreshTokenEntity {
            return RefreshTokenEntity(
                id = refreshToken.id,
                userId = refreshToken.userId,
                token = refreshToken.token,
                expiryDate = refreshToken.expiryDate,
                revoked = refreshToken.revoked
            )
        }
    }
}
