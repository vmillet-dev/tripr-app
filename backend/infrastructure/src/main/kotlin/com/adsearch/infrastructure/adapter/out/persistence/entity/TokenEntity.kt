package com.adsearch.infrastructure.adapter.out.persistence.entity

import com.adsearch.domain.model.enums.TokenTypeEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "T_TOKEN")
class TokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TKN_ID")
    var id: Long = 0,

    @Column(name = "USR_ID", nullable = false)
    var userId: Long,

    @Column(name = "TKN_VALUE", unique = true, nullable = false, length = 255)
    var token: String,

    @Column(name = "TKN_EXPIRY_DATE", nullable = false)
    var expiryDate: Instant,

    @Column(name = "TKN_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    var type: TokenTypeEnum,

    @Column(name = "TKN_REVOKED", nullable = false)
    var revoked: Boolean = false
)
