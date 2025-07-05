package com.adsearch.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "T_PASSWORD_RESET_TOKEN")
data class PasswordResetTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRT_ID")
    val id: Long = 0,

    @Column(name = "USR_ID", nullable = false, unique = true)
    val userId: Long,

    @Column(name = "PRT_TOKEN", unique = true, nullable = false, length = 255)
    val token: String,

    @Column(name = "PRT_EXPIRY_DATE", nullable = false)
    val expiryDate: Instant
)
