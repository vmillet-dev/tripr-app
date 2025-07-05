package com.adsearch.infrastructure.adapter.out.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "T_USER")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USR_ID")
    val id: Long = 0,

    @Column(name = "USR_USERNAME", unique = true, nullable = false, length = 255)
    val username: String,

    @Column(name = "USR_EMAIL", unique = true, nullable = false, length = 255)
    val email: String,

    @Column(name = "USR_PASSWORD", nullable = false, length = 255)
    val password: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "T_USER_ROLE",
        joinColumns = [JoinColumn(name = "USR_ID")],
        inverseJoinColumns = [JoinColumn(name = "ROLE_ID")]
    )
    val roles: MutableSet<RoleEntity> = HashSet()
)
