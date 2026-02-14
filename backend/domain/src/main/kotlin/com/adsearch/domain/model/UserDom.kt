package com.adsearch.domain.model

import com.adsearch.domain.model.command.RegisterUserCommand
import com.adsearch.domain.model.enums.UserRoleEnum

/**
 * Domain model representing a user in the system
 */
data class UserDom(
    val id: Long,
    val username: String,
    val email: String,
    val password: String,
    val roles: Set<String>,
    val enabled: Boolean
) {
    fun changePassword(newPassword: String): UserDom {
        val updatedUser = UserDom(id, username, email, newPassword, roles, enabled)
        return updatedUser
    }

    companion object {
        fun register(registerCommand: RegisterUserCommand): UserDom {
            return UserDom(
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
