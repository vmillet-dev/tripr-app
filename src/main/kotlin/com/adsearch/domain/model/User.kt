package com.adsearch.domain.model

import java.util.UUID

/**
 * Domain model representing a user in the system
 */
data class User(
    val id: UUID = UUID.randomUUID(),
    val username: String,
    val password: String,
    val roles: MutableList<String> = mutableListOf("USER"),
    val enabled: Boolean = true
)
