package com.adsearch.infrastructure.repository.entity

import com.adsearch.domain.model.RefreshToken
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "refresh_tokens")
class RefreshTokenEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false)
    val userId: UUID,
    
    @Column(nullable = false, unique = true)
    val token: String,
    
    @Column(nullable = false)
    val expiryDate: Instant,
    
    @Column(nullable = false)
    val revoked: Boolean = false
) {
    // Default constructor required by JPA
    constructor() : this(
        id = UUID.randomUUID(),
        userId = UUID.randomUUID(),
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
