package com.adsearch.infrastructure.adapter.out.persistence.entity

import com.adsearch.domain.model.PasswordResetToken
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "password_reset_tokens")
class PasswordResetTokenEntity(
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
    val used: Boolean = false
)
