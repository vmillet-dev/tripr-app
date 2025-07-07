package com.adsearch.application.impl

import com.adsearch.domain.command.RegisterUserCommand
import com.adsearch.domain.exception.EmailAlreadyExistsException
import com.adsearch.domain.exception.UsernameAlreadyExistsException
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.`in`.AuthenticationServicePort
import com.adsearch.domain.port.out.UserPersistencePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

@DisplayName("RegisterUseCaseImpl Tests")
class RegisterUseCaseImplTest {

    private val userPersistence: UserPersistencePort = mockk()
    private val authenticationService: AuthenticationServicePort = mockk()

    private lateinit var registerUseCase: RegisterUseCaseImpl

    @BeforeEach
    fun setUp() {
        registerUseCase = RegisterUseCaseImpl(userPersistence, authenticationService)
    }

    @Test
    @DisplayName("Should successfully register new user with valid data")
    fun shouldSuccessfullyRegisterNewUserWithValidData() {
        // Given
        val command = RegisterUserCommand("newuser", "test@example.com", "password123")
        val hashedPassword = "hashedPassword123"

        every { userPersistence.findByUsername(command.username) } returns null
        every { userPersistence.findByEmail(command.email) } returns null
        every { authenticationService.generateHashedPassword(command.password) } returns hashedPassword
        every { userPersistence.save(any<UserDom>()) } returns Unit

        // When
        registerUseCase.register(command)

        // Then
        verify { userPersistence.findByUsername(command.username) }
        verify { userPersistence.findByEmail(command.email) }
        verify { authenticationService.generateHashedPassword("password123") }
        verify { userPersistence.save(any<UserDom>()) }
    }

    @Test
    @DisplayName("Should throw UsernameAlreadyExistsException when username already exists")
    fun shouldThrowUsernameAlreadyExistsExceptionWhenUsernameAlreadyExists() {
        // Given
        val command = RegisterUserCommand("existinguser", "test@example.com", "password123")
        val existingUser = UserDom(1L, "existinguser", "existing@example.com", "hashedPassword", setOf("ROLE_USER"), true)

        every { userPersistence.findByUsername(command.username) } returns existingUser

        // When & Then
        val exception = assertThrows<UsernameAlreadyExistsException> {
            registerUseCase.register(command)
        }

        assertEquals("Registration failed - username ${command.username} already exists", exception.message)
        
        verify { userPersistence.findByUsername(command.username) }
        verify(exactly = 0) { userPersistence.findByEmail(any()) }
        verify(exactly = 0) { authenticationService.generateHashedPassword(any()) }
        verify(exactly = 0) { userPersistence.save(any<UserDom>()) }
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    fun shouldThrowEmailAlreadyExistsExceptionWhenEmailAlreadyExists() {
        // Given
        val command = RegisterUserCommand("newuser", "existing@example.com", "password123")
        val existingUser = UserDom(1L, "existinguser", "existing@example.com", "hashedPassword", setOf("ROLE_USER"), true)

        every { userPersistence.findByUsername(command.username) } returns null
        every { userPersistence.findByEmail(command.email) } returns existingUser

        // When & Then
        val exception = assertThrows<EmailAlreadyExistsException> {
            registerUseCase.register(command)
        }

        assertEquals("Registration failed - email ${command.email} already exists", exception.message)
        
        verify { userPersistence.findByUsername(command.username) }
        verify { userPersistence.findByEmail(command.email) }
        verify(exactly = 0) { authenticationService.generateHashedPassword(any()) }
        verify(exactly = 0) { userPersistence.save(any<UserDom>()) }
    }

    @Test
    @DisplayName("Should hash password before saving user")
    fun shouldHashPasswordBeforeSavingUser() {
        // Given
        val originalPassword = "plainTextPassword"
        val command = RegisterUserCommand("newuser", "test@example.com", originalPassword)
        val hashedPassword = "hashedPassword123"
        val capturedUsers = mutableListOf<UserDom>()

        every { userPersistence.findByUsername(command.username) } returns null
        every { userPersistence.findByEmail(command.email) } returns null
        every { authenticationService.generateHashedPassword(originalPassword) } returns hashedPassword
        every { userPersistence.save(capture(capturedUsers)) } returns Unit

        // When
        registerUseCase.register(command)

        // Then
        val savedUser = capturedUsers.first()
        assertEquals(hashedPassword, savedUser.password)
        assertEquals(command.username, savedUser.username)
        assertEquals(command.email, savedUser.email)
        assertEquals(setOf("ROLE_USER"), savedUser.roles)
        assertEquals(true, savedUser.enabled)
        
        verify { authenticationService.generateHashedPassword(originalPassword) }
    }

    @Test
    @DisplayName("Should check username availability before checking email")
    fun shouldCheckUsernameAvailabilityBeforeCheckingEmail() {
        // Given
        val command = RegisterUserCommand("existinguser", "test@example.com", "password123")
        val existingUser = UserDom(1L, "existinguser", "existing@example.com", "hashedPassword", setOf("ROLE_USER"), true)

        every { userPersistence.findByUsername(command.username) } returns existingUser

        // When & Then
        assertThrows<UsernameAlreadyExistsException> {
            registerUseCase.register(command)
        }

        verify { userPersistence.findByUsername(command.username) }
        verify(exactly = 0) { userPersistence.findByEmail(any()) }
    }

    @Test
    @DisplayName("Should create user with default role and enabled status")
    fun shouldCreateUserWithDefaultRoleAndEnabledStatus() {
        // Given
        val command = RegisterUserCommand("newuser", "test@example.com", "password123")
        val hashedPassword = "hashedPassword123"
        val capturedUsers = mutableListOf<UserDom>()

        every { userPersistence.findByUsername(command.username) } returns null
        every { userPersistence.findByEmail(command.email) } returns null
        every { authenticationService.generateHashedPassword(command.password) } returns hashedPassword
        every { userPersistence.save(capture(capturedUsers)) } returns Unit

        // When
        registerUseCase.register(command)

        // Then
        val savedUser = capturedUsers.first()
        assertEquals(setOf("ROLE_USER"), savedUser.roles)
        assertEquals(true, savedUser.enabled)
        assertEquals(0L, savedUser.id) // New user should have id 0
    }
}
