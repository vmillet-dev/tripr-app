package com.adsearch.domain.model

import com.adsearch.domain.enum.UserRoleEnum

/**
 * Domain model representing a user in the system
 */
data class UserDom(
    val id: Long = 0,
    val username: String,
    val email: String,
    var password: String,
    val roles: List<String> = listOf(UserRoleEnum.ROLE_USER.type),
    val enabled: Boolean = true
)
