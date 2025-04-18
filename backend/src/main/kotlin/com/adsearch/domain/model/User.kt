package com.adsearch.domain.model

/**
 * Domain model representing a user in the system
 */
data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val roles: List<String> = listOf("USER"),
    val enabled: Boolean = true
)
