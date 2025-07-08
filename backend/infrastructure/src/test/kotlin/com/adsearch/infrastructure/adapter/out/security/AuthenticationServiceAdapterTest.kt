package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import com.adsearch.infrastructure.service.AuthenticationService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Authentication Service Adapter Tests")
class AuthenticationServiceAdapterTest {

    private lateinit var authenticationServiceAdapter: AuthenticationServiceAdapter
    private val authenticationService = mockk<AuthenticationService>()
    private val userRepository = mockk<UserRepository>()
    private val userEntityMapper = mockk<UserEntityMapper>()

    @BeforeEach
    fun setUp() {
        authenticationServiceAdapter = AuthenticationServiceAdapter(
            authenticationService,
            userRepository,
            userEntityMapper
        )
    }

    @Test
    @DisplayName("Should authenticate user and return user domain when credentials are valid")
    fun shouldAuthenticateUserAndReturnUserDomainWhenCredentialsAreValid() {
        // Given
        val username = "testuser"
        val password = "testpassword"
        val authenticatedUsername = "testuser"
        val userEntity = UserEntity(
            id = 1L,
            username = authenticatedUsername,
            email = "test@example.com",
            password = "hashedpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 1L,
            username = authenticatedUsername,
            email = "test@example.com",
            password = "hashedpassword",
            roles = setOf("USER"),
            enabled = true
        )

        every { authenticationService.authenticate(username, password) } returns authenticatedUsername
        every { userRepository.findByUsername(authenticatedUsername) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = authenticationServiceAdapter.authenticate(username, password)

        // Then
        assertNotNull(result)
        assertEquals(userDom, result)
        verify(exactly = 1) { authenticationService.authenticate(username, password) }
        verify(exactly = 1) { userRepository.findByUsername(authenticatedUsername) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should generate hashed password when requested")
    fun shouldGenerateHashedPasswordWhenRequested() {
        // Given
        val plainPassword = "plainpassword123"
        val hashedPassword = "\$2a\$10\$hashedpasswordexample"

        every { authenticationService.generateHashedPassword(plainPassword) } returns hashedPassword

        // When
        val result = authenticationServiceAdapter.generateHashedPassword(plainPassword)

        // Then
        assertEquals(hashedPassword, result)
        verify(exactly = 1) { authenticationService.generateHashedPassword(plainPassword) }
    }

    @Test
    @DisplayName("Should delegate authentication to underlying service")
    fun shouldDelegateAuthenticationToUnderlyingService() {
        // Given
        val username = "delegateuser"
        val password = "delegatepassword"
        val authenticatedUsername = "delegateuser"
        val userEntity = UserEntity(
            id = 2L,
            username = authenticatedUsername,
            email = "delegate@example.com",
            password = "delegatehashedpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 2L,
            username = authenticatedUsername,
            email = "delegate@example.com",
            password = "delegatehashedpassword",
            roles = setOf("ADMIN"),
            enabled = true
        )

        every { authenticationService.authenticate(username, password) } returns authenticatedUsername
        every { userRepository.findByUsername(authenticatedUsername) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = authenticationServiceAdapter.authenticate(username, password)

        // Then
        assertEquals(userDom, result)
        verify(exactly = 1) { authenticationService.authenticate(username, password) }
        verify(exactly = 1) { userRepository.findByUsername(authenticatedUsername) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should delegate password hashing to underlying service")
    fun shouldDelegatePasswordHashingToUnderlyingService() {
        // Given
        val password = "passwordtohash"
        val expectedHash = "\$2a\$10\$anotherhashexample"

        every { authenticationService.generateHashedPassword(password) } returns expectedHash

        // When
        val result = authenticationServiceAdapter.generateHashedPassword(password)

        // Then
        assertEquals(expectedHash, result)
        verify(exactly = 1) { authenticationService.generateHashedPassword(password) }
    }

    @Test
    @DisplayName("Should handle authentication with different user roles correctly")
    fun shouldHandleAuthenticationWithDifferentUserRolesCorrectly() {
        // Given
        val username = "adminuser"
        val password = "adminpassword"
        val authenticatedUsername = "adminuser"
        val userEntity = UserEntity(
            id = 3L,
            username = authenticatedUsername,
            email = "admin@example.com",
            password = "adminhashedpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 3L,
            username = authenticatedUsername,
            email = "admin@example.com",
            password = "adminhashedpassword",
            roles = setOf("ADMIN", "USER"),
            enabled = true
        )

        every { authenticationService.authenticate(username, password) } returns authenticatedUsername
        every { userRepository.findByUsername(authenticatedUsername) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = authenticationServiceAdapter.authenticate(username, password)

        // Then
        assertEquals(userDom, result)
        assertEquals(setOf("ADMIN", "USER"), result.roles)
        verify(exactly = 1) { authenticationService.authenticate(username, password) }
        verify(exactly = 1) { userRepository.findByUsername(authenticatedUsername) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should handle disabled user authentication correctly")
    fun shouldHandleDisabledUserAuthenticationCorrectly() {
        // Given
        val username = "disableduser"
        val password = "disabledpassword"
        val authenticatedUsername = "disableduser"
        val userEntity = UserEntity(
            id = 4L,
            username = authenticatedUsername,
            email = "disabled@example.com",
            password = "disabledhashedpassword",
            enabled = false,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 4L,
            username = authenticatedUsername,
            email = "disabled@example.com",
            password = "disabledhashedpassword",
            roles = setOf("USER"),
            enabled = false
        )

        every { authenticationService.authenticate(username, password) } returns authenticatedUsername
        every { userRepository.findByUsername(authenticatedUsername) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = authenticationServiceAdapter.authenticate(username, password)

        // Then
        assertEquals(userDom, result)
        assertEquals(false, result.enabled)
        verify(exactly = 1) { authenticationService.authenticate(username, password) }
        verify(exactly = 1) { userRepository.findByUsername(authenticatedUsername) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should handle empty password hashing correctly")
    fun shouldHandleEmptyPasswordHashingCorrectly() {
        // Given
        val emptyPassword = ""
        val hashedEmptyPassword = "\$2a\$10\$emptypasswordhash"

        every { authenticationService.generateHashedPassword(emptyPassword) } returns hashedEmptyPassword

        // When
        val result = authenticationServiceAdapter.generateHashedPassword(emptyPassword)

        // Then
        assertEquals(hashedEmptyPassword, result)
        verify(exactly = 1) { authenticationService.generateHashedPassword(emptyPassword) }
    }

    @Test
    @DisplayName("Should handle long password hashing correctly")
    fun shouldHandleLongPasswordHashingCorrectly() {
        // Given
        val longPassword = "verylongpasswordwithmanycharsandnumbers123456789abcdefghijklmnopqrstuvwxyz"
        val hashedLongPassword = "\$2a\$10\$longpasswordhashexample"

        every { authenticationService.generateHashedPassword(longPassword) } returns hashedLongPassword

        // When
        val result = authenticationServiceAdapter.generateHashedPassword(longPassword)

        // Then
        assertEquals(hashedLongPassword, result)
        verify(exactly = 1) { authenticationService.generateHashedPassword(longPassword) }
    }

    @Test
    @DisplayName("Should handle special characters in password hashing correctly")
    fun shouldHandleSpecialCharactersInPasswordHashingCorrectly() {
        // Given
        val specialPassword = "password!@#$%^&*()_+-=[]{}|;':\",./<>?"
        val hashedSpecialPassword = "\$2a\$10\$specialpasswordhash"

        every { authenticationService.generateHashedPassword(specialPassword) } returns hashedSpecialPassword

        // When
        val result = authenticationServiceAdapter.generateHashedPassword(specialPassword)

        // Then
        assertEquals(hashedSpecialPassword, result)
        verify(exactly = 1) { authenticationService.generateHashedPassword(specialPassword) }
    }

    @Test
    @DisplayName("Should maintain consistent behavior across multiple authentication calls")
    fun shouldMaintainConsistentBehaviorAcrossMultipleAuthenticationCalls() {
        // Given
        val username = "consistentuser"
        val password = "consistentpassword"
        val authenticatedUsername = "consistentuser"
        val userEntity = UserEntity(
            id = 5L,
            username = authenticatedUsername,
            email = "consistent@example.com",
            password = "consistenthashedpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 5L,
            username = authenticatedUsername,
            email = "consistent@example.com",
            password = "consistenthashedpassword",
            roles = setOf("USER"),
            enabled = true
        )

        every { authenticationService.authenticate(username, password) } returns authenticatedUsername
        every { userRepository.findByUsername(authenticatedUsername) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result1 = authenticationServiceAdapter.authenticate(username, password)
        val result2 = authenticationServiceAdapter.authenticate(username, password)

        // Then
        assertEquals(userDom, result1)
        assertEquals(userDom, result2)
        assertEquals(result1, result2)
        verify(exactly = 2) { authenticationService.authenticate(username, password) }
        verify(exactly = 2) { userRepository.findByUsername(authenticatedUsername) }
        verify(exactly = 2) { userEntityMapper.toDomain(userEntity) }
    }
}
