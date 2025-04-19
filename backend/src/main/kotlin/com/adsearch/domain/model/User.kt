package com.adsearch.domain.model

/**
 * Domain model representing a user in the system
 */
data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val roles: Set<Role> = setOf(Role(name = RoleType.USER)),
    val enabled: Boolean = true
)
