package com.adsearch.domain.model

import com.adsearch.domain.command.RegisterUserCommand
import com.adsearch.domain.model.enum.UserRoleEnum

/**
 * Domain model representing a user in the system
 */
data class UserDom(
    val username: String,
    val email: String,
    val password: String,
    val roles: Set<String>,
    val enabled: Boolean
) {
    fun changePassword(newPassword: String): UserDom {
        val updatedUser = UserDom(username, email, newPassword, roles, enabled)
        return updatedUser
    }

    companion object {
        fun register(registerCommand: RegisterUserCommand): UserDom {
            return UserDom(
                registerCommand.username,
                registerCommand.email,
                registerCommand.password,
                setOf(UserRoleEnum.ROLE_USER.type),
                true
            )
        }
    }
}
