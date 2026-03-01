package com.adsearch.domain.service

import com.adsearch.domain.enums.UserRoleEnum
import com.adsearch.domain.exception.EmailAlreadyExistsException
import com.adsearch.domain.exception.UsernameAlreadyExistsException
import com.adsearch.domain.model.User
import com.adsearch.domain.port.`in`.CreateUserUseCase
import com.adsearch.domain.port.out.authentication.PasswordEncoderPort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UserServiceTest {

    private val passwordEncoder = mockk<PasswordEncoderPort>(relaxed = true)
    private val userPersistence = mockk<UserPersistencePort>(relaxed = true)

    private val userService = UserService(passwordEncoder, userPersistence)

    @Test
    fun `createUser should save user when username and email do not exist`() {
        // Given
        val cmd = CreateUserUseCase.RegisterUserCommand("newuser", "new@example.com", "plainpwd")
        every { userPersistence.findByUsername("newuser") } returns null
        every { userPersistence.findByEmail("new@example.com") } returns null
        every { passwordEncoder.encode("plainpwd") } returns "encodedpwd"

        // When
        userService.createUser(cmd)

        // Then
        verify {
            userPersistence.save(withArg { user: User ->
                assertThat(user.username).isEqualTo("newuser")
                assertThat(user.email).isEqualTo("new@example.com")
                assertThat(user.password).isEqualTo("encodedpwd")
                assertThat(user.roles).contains(UserRoleEnum.ROLE_USER.type)
                assertThat(user.id).isZero()
                assertThat(user.enabled).isTrue()
            })
        }

        assertThat(cmd.password).isEqualTo("encodedpwd")
    }

    @Test
    fun `createUser should throw UsernameAlreadyExistsException when username exists`() {
        // Given
        val cmd = CreateUserUseCase.RegisterUserCommand("existing", "e@e.com", "pwd")
        every { userPersistence.findByUsername("existing") } returns User(
            1,
            "existing",
            "e@e.com",
            "pwd",
            setOf(UserRoleEnum.ROLE_USER.type),
            true
        )

        // When / Then
        assertThatThrownBy { userService.createUser(cmd) }
            .isInstanceOf(UsernameAlreadyExistsException::class.java)
            .hasMessageContaining("existing")

        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userPersistence.save(any()) }
    }

    @Test
    fun `createUser should throw EmailAlreadyExistsException when email exists`() {
        // Given
        val cmd = CreateUserUseCase.RegisterUserCommand("newname", "exists@e.com", "pwd")
        every { userPersistence.findByUsername("newname") } returns null
        every { userPersistence.findByEmail("exists@e.com") } returns User(
            2,
            "other",
            "exists@e.com",
            "pwd",
            setOf(UserRoleEnum.ROLE_USER.type),
            true
        )

        // When / Then
        assertThatThrownBy { userService.createUser(cmd) }
            .isInstanceOf(EmailAlreadyExistsException::class.java)
            .hasMessageContaining("exists@e.com")

        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userPersistence.save(any()) }
    }
}

