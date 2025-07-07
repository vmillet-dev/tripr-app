package com.adsearch.infrastructure.service

import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("JWT User Details Service Tests")
class JwtUserDetailsServiceTest {

    private lateinit var jwtUserDetailsService: JwtUserDetailsService
    private val userRepository = mockk<UserRepository>()

    @BeforeEach
    fun setUp() {
        jwtUserDetailsService = JwtUserDetailsService(userRepository)
    }

    @Test
    @DisplayName("Should load user by username when user exists")
    fun shouldLoadUserByUsernameWhenUserExists() {
        // Given
        val username = "testuser"
        val userRole = RoleEntity(id = 1L, type = "USER")
        val adminRole = RoleEntity(id = 2L, type = "ADMIN")
        val userEntity = UserEntity(
            id = 1L,
            username = username,
            email = "test@example.com",
            password = "hashedpassword",
            enabled = true,
            roles = mutableSetOf(userRole, adminRole)
        )

        every { userRepository.findByUsername(username) } returns userEntity

        // When
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)

        // Then
        assertNotNull(userDetails)
        assertEquals(username, userDetails.username)
        assertEquals("hashedpassword", userDetails.password)
        assertTrue(userDetails.isEnabled)
        assertEquals(2, userDetails.authorities.size)
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("USER")))
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("ADMIN")))
        
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    @DisplayName("Should load user with single role when user has one role")
    fun shouldLoadUserWithSingleRoleWhenUserHasOneRole() {
        // Given
        val username = "singleuser"
        val userRole = RoleEntity(id = 1L, type = "USER")
        val userEntity = UserEntity(
            id = 2L,
            username = username,
            email = "single@example.com",
            password = "singlepassword",
            enabled = true,
            roles = mutableSetOf(userRole)
        )

        every { userRepository.findByUsername(username) } returns userEntity

        // When
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)

        // Then
        assertNotNull(userDetails)
        assertEquals(username, userDetails.username)
        assertEquals("singlepassword", userDetails.password)
        assertEquals(1, userDetails.authorities.size)
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("USER")))
        
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    @DisplayName("Should load user with no roles when user has empty roles")
    fun shouldLoadUserWithNoRolesWhenUserHasEmptyRoles() {
        // Given
        val username = "noroleuser"
        val userEntity = UserEntity(
            id = 3L,
            username = username,
            email = "norole@example.com",
            password = "norolepassword",
            enabled = true,
            roles = mutableSetOf()
        )

        every { userRepository.findByUsername(username) } returns userEntity

        // When
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)

        // Then
        assertNotNull(userDetails)
        assertEquals(username, userDetails.username)
        assertEquals("norolepassword", userDetails.password)
        assertEquals(0, userDetails.authorities.size)
        
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    fun shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        // Given
        val nonExistentUsername = "nonexistent"
        every { userRepository.findByUsername(nonExistentUsername) } returns null

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            jwtUserDetailsService.loadUserByUsername(nonExistentUsername)
        }
        
        assertTrue(exception.message!!.contains("UserEntity $nonExistentUsername not found"))
        verify(exactly = 1) { userRepository.findByUsername(nonExistentUsername) }
    }

    @Test
    @DisplayName("Should handle disabled user correctly")
    fun shouldHandleDisabledUserCorrectly() {
        // Given
        val username = "disableduser"
        val userRole = RoleEntity(id = 1L, type = "USER")
        val userEntity = UserEntity(
            id = 4L,
            username = username,
            email = "disabled@example.com",
            password = "disabledpassword",
            enabled = false,
            roles = mutableSetOf(userRole)
        )

        every { userRepository.findByUsername(username) } returns userEntity

        // When
        val userDetails = jwtUserDetailsService.loadUserByUsername(username)

        // Then
        assertNotNull(userDetails)
        assertEquals(username, userDetails.username)
        assertEquals("disabledpassword", userDetails.password)
        assertEquals(1, userDetails.authorities.size)
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("USER")))
        
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }

    @Test
    @DisplayName("Should call repository exactly once for each username lookup")
    fun shouldCallRepositoryExactlyOnceForEachUsernameLookup() {
        // Given
        val username = "testrepocall"
        val userRole = RoleEntity(id = 1L, type = "USER")
        val userEntity = UserEntity(
            id = 5L,
            username = username,
            email = "repo@example.com",
            password = "repopassword",
            enabled = true,
            roles = mutableSetOf(userRole)
        )

        every { userRepository.findByUsername(username) } returns userEntity

        // When
        jwtUserDetailsService.loadUserByUsername(username)

        // Then
        verify(exactly = 1) { userRepository.findByUsername(username) }
    }
}
