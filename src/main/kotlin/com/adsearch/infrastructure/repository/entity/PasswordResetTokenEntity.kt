package com.adsearch.infrastructure.repository.entity

import com.adsearch.domain.model.PasswordResetToken
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "password_reset_tokens")
class PasswordResetTokenEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(nullable = false)
    val userId: UUID,
    
    @Column(nullable = false, unique = true)
    val token: String,
    
    @Column(nullable = false)
    val expiryDate: Instant,
    
    @Column(nullable = false)
    val used: Boolean = false
) {
    // Default constructor required by JPA
    constructor() : this(
        id = UUID.randomUUID(),
        userId = UUID.randomUUID(),
        token = "",
        expiryDate = Instant.now(),
        used = false
    )
    
    fun toDomain(): PasswordResetToken {
        return PasswordResetToken(
            id = id,
            userId = userId,
            token = token,
            expiryDate = expiryDate,
            used = used
        )
    }
    
    companion object {
        fun fromDomain(passwordResetToken: PasswordResetToken): PasswordResetTokenEntity {
            return PasswordResetTokenEntity(
                id = passwordResetToken.id,
                userId = passwordResetToken.userId,
                token = passwordResetToken.token,
                expiryDate = passwordResetToken.expiryDate,
                used = passwordResetToken.used
            )
        }
    }
}
