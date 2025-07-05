package com.adsearch.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_ROLE")
class RoleEntity(
    @Id
    @Column(name = "ROLE_ID")
    val id: Long = 0,

    @Column(name = "ROLE_TYPE", unique = true, nullable = false, length = 50)
    val type: String,
)
