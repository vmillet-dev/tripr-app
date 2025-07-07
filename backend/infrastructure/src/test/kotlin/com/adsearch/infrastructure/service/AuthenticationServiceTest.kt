package com.adsearch.infrastructure.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Authentication Service Tests")
class AuthenticationServiceTest {

    private lateinit var authenticationService: AuthenticationService
    private val authenticationManager = mockk<AuthenticationManager>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val authentication = mockk<Authentication>()

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(authenticationManager, passwordEncoder)
    }

    @Test
    @DisplayName("Should authenticate user successfully when valid credentials are provided")
    fun shouldAuthenticateUserSuccessfullyWhenValidCredentialsAreProvided() {
        // Given
        val username = "testuser"
        val password = "testpassword"
        val expectedAuthenticatedName = "testuser"

        every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication
        every { authentication.name } returns expectedAuthenticatedName

        // When
        val authenticatedUsername = authenticationService.authenticate(username, password)

        // Then
        assertEquals(expectedAuthenticatedName, authenticatedUsername)
        verify(exactly = 1) { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) }
        verify(exactly = 1) { authentication.name }
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when invalid credentials are provided")
    fun shouldThrowBadCredentialsExceptionWhenInvalidCredentialsAreProvided() {
        // Given
        val username = "invaliduser"
        val password = "invalidpassword"

        every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } throws BadCredentialsException("Bad credentials")

        // When & Then
        val exception = assertThrows<BadCredentialsException> {
            authenticationService.authenticate(username, password)
        }
        
        assertEquals("Bad credentials", exception.message)
        verify(exactly = 1) { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) }
    }

    @Test
    @DisplayName("Should create correct authentication token with username and password")
    fun shouldCreateCorrectAuthenticationTokenWithUsernameAndPassword() {
        // Given
        val username = "tokenuser"
        val password = "tokenpassword"
        val expectedAuthenticatedName = "tokenuser"

        every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication
        every { authentication.name } returns expectedAuthenticatedName

        // When
        authenticationService.authenticate(username, password)

        // Then
        verify(exactly = 1) { 
            authenticationManager.authenticate(match<UsernamePasswordAuthenticationToken> { token ->
                token.principal == username && token.credentials == password
            })
        }
    }

    @Test
    @DisplayName("Should generate hashed password when plain password is provided")
    fun shouldGenerateHashedPasswordWhenPlainPasswordIsProvided() {
        // Given
        val plainPassword = "plainpassword"
        val expectedHashedPassword = "\$2a\$10\$hashedpasswordexample"

        every { passwordEncoder.encode(plainPassword) } returns expectedHashedPassword

        // When
        val hashedPassword = authenticationService.generateHashedPassword(plainPassword)

        // Then
        assertEquals(expectedHashedPassword, hashedPassword)
        verify(exactly = 1) { passwordEncoder.encode(plainPassword) }
    }

    @Test
    @DisplayName("Should generate different hashed passwords for different plain passwords")
    fun shouldGenerateDifferentHashedPasswordsForDifferentPlainPasswords() {
        // Given
        val password1 = "password1"
        val password2 = "password2"
        val hashedPassword1 = "\$2a\$10\$hashedpassword1"
        val hashedPassword2 = "\$2a\$10\$hashedpassword2"

        every { passwordEncoder.encode(password1) } returns hashedPassword1
        every { passwordEncoder.encode(password2) } returns hashedPassword2

        // When
        val result1 = authenticationService.generateHashedPassword(password1)
        val result2 = authenticationService.generateHashedPassword(password2)

        // Then
        assertEquals(hashedPassword1, result1)
        assertEquals(hashedPassword2, result2)
        assertTrue(result1 != result2)
        verify(exactly = 1) { passwordEncoder.encode(password1) }
        verify(exactly = 1) { passwordEncoder.encode(password2) }
    }

    @Test
    @DisplayName("Should handle empty password correctly")
    fun shouldHandleEmptyPasswordCorrectly() {
        // Given
        val emptyPassword = ""
        val hashedEmptyPassword = "\$2a\$10\$hashedemptypassword"

        every { passwordEncoder.encode(emptyPassword) } returns hashedEmptyPassword

        // When
        val hashedPassword = authenticationService.generateHashedPassword(emptyPassword)

        // Then
        assertEquals(hashedEmptyPassword, hashedPassword)
        verify(exactly = 1) { passwordEncoder.encode(emptyPassword) }
    }

    @Test
    @DisplayName("Should return authenticated username from authentication object")
    fun shouldReturnAuthenticatedUsernameFromAuthenticationObject() {
        // Given
        val username = "authuser"
        val password = "authpassword"
        val authenticatedName = "authenticated_authuser"

        every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication
        every { authentication.name } returns authenticatedName

        // When
        val result = authenticationService.authenticate(username, password)

        // Then
        assertEquals(authenticatedName, result)
        assertNotNull(result)
        verify(exactly = 1) { authentication.name }
    }
}
