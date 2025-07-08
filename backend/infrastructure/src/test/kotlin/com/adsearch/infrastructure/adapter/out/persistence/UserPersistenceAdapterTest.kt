package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("User Persistence Adapter Tests")
class UserPersistenceAdapterTest {

    private lateinit var userPersistenceAdapter: UserPersistenceAdapter
    private val userRepository = mockk<UserRepository>()
    private val userEntityMapper = mockk<UserEntityMapper>()

    @BeforeEach
    fun setUp() {
        userPersistenceAdapter = UserPersistenceAdapter(userRepository, userEntityMapper)
    }

    @Test
    @DisplayName("Should save user domain when save is called")
    fun shouldSaveUserDomainWhenSaveIsCalled() {
        // Given
        val userDom = UserDom(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            roles = setOf("USER"),
            enabled = true
        )
        val userEntity = UserEntity(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            enabled = true,
            roles = mutableSetOf()
        )

        every { userEntityMapper.toEntity(userDom) } returns userEntity
        every { userRepository.save(userEntity) } returns userEntity

        // When
        userPersistenceAdapter.save(userDom)

        // Then
        verify(exactly = 1) { userEntityMapper.toEntity(userDom) }
        verify(exactly = 1) { userRepository.save(userEntity) }
    }

    @Test
    @DisplayName("Should find user by username when user exists")
    fun shouldFindUserByUsernameWhenUserExists() {
        // Given
        val username = "existinguser"
        val userEntity = UserEntity(
            id = 2L,
            username = username,
            email = "existing@example.com",
            password = "existingpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 2L,
            username = username,
            email = "existing@example.com",
            password = "existingpassword",
            roles = setOf("USER"),
            enabled = true
        )

        every { userRepository.findByUsername(username) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = userPersistenceAdapter.findByUsername(username)

        // Then
        assertNotNull(result)
        assertEquals(userDom, result)
        verify(exactly = 1) { userRepository.findByUsername(username) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should return null when user by username does not exist")
    fun shouldReturnNullWhenUserByUsernameDoesNotExist() {
        // Given
        val nonExistentUsername = "nonexistent"

        every { userRepository.findByUsername(nonExistentUsername) } returns null

        // When
        val result = userPersistenceAdapter.findByUsername(nonExistentUsername)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findByUsername(nonExistentUsername) }
        verify(exactly = 0) { userEntityMapper.toDomain(any()) }
    }

    @Test
    @DisplayName("Should find user by id when user exists")
    fun shouldFindUserByIdWhenUserExists() {
        // Given
        val userId = 3L
        val userEntity = UserEntity(
            id = userId,
            username = "iduser",
            email = "id@example.com",
            password = "idpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = userId,
            username = "iduser",
            email = "id@example.com",
            password = "idpassword",
            roles = setOf("ADMIN"),
            enabled = true
        )

        every { userRepository.findById(userId) } returns Optional.of(userEntity)
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = userPersistenceAdapter.findById(userId)

        // Then
        assertNotNull(result)
        assertEquals(userDom, result)
        verify(exactly = 1) { userRepository.findById(userId) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should return null when user by id does not exist")
    fun shouldReturnNullWhenUserByIdDoesNotExist() {
        // Given
        val nonExistentId = 999L

        every { userRepository.findById(nonExistentId) } returns Optional.empty()

        // When
        val result = userPersistenceAdapter.findById(nonExistentId)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findById(nonExistentId) }
        verify(exactly = 0) { userEntityMapper.toDomain(any()) }
    }

    @Test
    @DisplayName("Should find user by email when user exists")
    fun shouldFindUserByEmailWhenUserExists() {
        // Given
        val email = "email@example.com"
        val userEntity = UserEntity(
            id = 4L,
            username = "emailuser",
            email = email,
            password = "emailpassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = 4L,
            username = "emailuser",
            email = email,
            password = "emailpassword",
            roles = setOf("USER", "ADMIN"),
            enabled = true
        )

        every { userRepository.findByEmail(email) } returns userEntity
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val result = userPersistenceAdapter.findByEmail(email)

        // Then
        assertNotNull(result)
        assertEquals(userDom, result)
        verify(exactly = 1) { userRepository.findByEmail(email) }
        verify(exactly = 1) { userEntityMapper.toDomain(userEntity) }
    }

    @Test
    @DisplayName("Should return null when user by email does not exist")
    fun shouldReturnNullWhenUserByEmailDoesNotExist() {
        // Given
        val nonExistentEmail = "nonexistent@example.com"

        every { userRepository.findByEmail(nonExistentEmail) } returns null

        // When
        val result = userPersistenceAdapter.findByEmail(nonExistentEmail)

        // Then
        assertNull(result)
        verify(exactly = 1) { userRepository.findByEmail(nonExistentEmail) }
        verify(exactly = 0) { userEntityMapper.toDomain(any()) }
    }

    @Test
    @DisplayName("Should delegate save operation to repository and mapper correctly")
    fun shouldDelegateSaveOperationToRepositoryAndMapperCorrectly() {
        // Given
        val userDom = UserDom(
            id = 5L,
            username = "delegateuser",
            email = "delegate@example.com",
            password = "delegatepassword",
            roles = setOf("USER"),
            enabled = false
        )
        val userEntity = UserEntity(
            id = 5L,
            username = "delegateuser",
            email = "delegate@example.com",
            password = "delegatepassword",
            enabled = false,
            roles = mutableSetOf()
        )

        every { userEntityMapper.toEntity(userDom) } returns userEntity
        every { userRepository.save(userEntity) } returns userEntity

        // When
        userPersistenceAdapter.save(userDom)

        // Then
        verify(exactly = 1) { userEntityMapper.toEntity(userDom) }
        verify(exactly = 1) { userRepository.save(userEntity) }
    }

    @Test
    @DisplayName("Should handle multiple find operations independently")
    fun shouldHandleMultipleFindOperationsIndependently() {
        // Given
        val username = "multiuser"
        val email = "multi@example.com"
        val id = 6L
        val userEntity = UserEntity(
            id = id,
            username = username,
            email = email,
            password = "multipassword",
            enabled = true,
            roles = mutableSetOf()
        )
        val userDom = UserDom(
            id = id,
            username = username,
            email = email,
            password = "multipassword",
            roles = setOf("USER"),
            enabled = true
        )

        every { userRepository.findByUsername(username) } returns userEntity
        every { userRepository.findByEmail(email) } returns userEntity
        every { userRepository.findById(id) } returns Optional.of(userEntity)
        every { userEntityMapper.toDomain(userEntity) } returns userDom

        // When
        val resultByUsername = userPersistenceAdapter.findByUsername(username)
        val resultByEmail = userPersistenceAdapter.findByEmail(email)
        val resultById = userPersistenceAdapter.findById(id)

        // Then
        assertEquals(userDom, resultByUsername)
        assertEquals(userDom, resultByEmail)
        assertEquals(userDom, resultById)
        verify(exactly = 1) { userRepository.findByUsername(username) }
        verify(exactly = 1) { userRepository.findByEmail(email) }
        verify(exactly = 1) { userRepository.findById(id) }
        verify(exactly = 3) { userEntityMapper.toDomain(userEntity) }
    }
}
