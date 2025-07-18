package com.adsearch.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "T_REFRESH_TOKEN")
class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RT_ID")
    val id: Long = 0,

    @Column(name = "USR_ID", nullable = false, unique = true)
    val userId: Long,

    @Column(name = "RT_TOKEN", unique = true, nullable = false, length = 255)
    val token: String,

    @Column(name = "RT_EXPIRY_DATE", nullable = false)
    val expiryDate: Instant,

    @Column(name = "RT_REVOKED", nullable = false)
    val revoked: Boolean = false
)
