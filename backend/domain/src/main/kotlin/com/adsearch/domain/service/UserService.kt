package com.adsearch.domain.service

import com.adsearch.domain.annotation.AutoRegister
import com.adsearch.domain.exception.EmailAlreadyExistsException
import com.adsearch.domain.exception.UsernameAlreadyExistsException
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.model.command.RegisterUserCommand
import com.adsearch.domain.port.`in`.CreateUserUseCase
import com.adsearch.domain.port.out.authentication.PasswordEncoderPort
import com.adsearch.domain.port.out.persistence.UserPersistencePort

@AutoRegister
@Suppress("unused")
class UserService(
    private val passwordEncoder: PasswordEncoderPort,
    private val userPersistence: UserPersistencePort
) : CreateUserUseCase {
    override fun createUser(cmd: RegisterUserCommand) {

        if (userPersistence.findByUsername(cmd.username) != null) {
            throw UsernameAlreadyExistsException("Registration failed - username ${cmd.username} already exists")
        }

        if (userPersistence.findByEmail(cmd.email) != null) {
            throw EmailAlreadyExistsException("Registration failed - email ${cmd.email} already exists")
        }

        cmd.apply { password = passwordEncoder.encode(cmd.password) }
        userPersistence.save(UserDom.register(cmd))
    }
}
