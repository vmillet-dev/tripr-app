package com.adsearch.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "roles")
class RoleEntity(
    @Id
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val type: String
)
