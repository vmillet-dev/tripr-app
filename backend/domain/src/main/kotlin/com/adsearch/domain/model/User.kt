package com.adsearch.domain.model

import com.adsearch.domain.enums.UserRoleEnum
import com.adsearch.domain.port.`in`.CreateUserUseCase

/**
 * Domain model representing a user in the system
 */
data class User(
    val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val roles: Set<String>,
    val enabled: Boolean
) {
    fun changePassword(newPassword: String): User {
        val updatedUser = User(id, username, email, newPassword, roles, enabled)
        return updatedUser
    }

    companion object {
        fun register(registerCommand: CreateUserUseCase.RegisterUserCommand): User {
            return User(
                0,
                registerCommand.username,
                registerCommand.email,
                registerCommand.password,
                setOf(UserRoleEnum.ROLE_USER.type),
                true
            )
        }
    }
}
