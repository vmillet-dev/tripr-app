package com.adsearch.application.impl

import com.adsearch.application.RegisterUseCase
import com.adsearch.application.annotation.AutoRegister
import com.adsearch.domain.command.RegisterUserCommand
import com.adsearch.domain.exception.EmailAlreadyExistsException
import com.adsearch.domain.exception.UsernameAlreadyExistsException
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.out.UserPersistencePort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@AutoRegister
@Suppress("unused")
class RegisterUseCaseImpl(
    private val userPersistence: UserPersistencePort,
    private val authenticationService: AuthenticationServicePort
) : RegisterUseCase {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun register(cmd: RegisterUserCommand) {

        if (userPersistence.findByUsername(cmd.username) != null) {
            throw UsernameAlreadyExistsException("Registration failed - username ${cmd.username} already exists")
        }

        if (userPersistence.findByEmail(cmd.email) != null) {
            throw EmailAlreadyExistsException("Registration failed - email ${cmd.email} already exists")
        }

        cmd.apply { password = authenticationService.generateHashedPassword(cmd.password) }
        userPersistence.save(UserDom.register(cmd))
    }
}
