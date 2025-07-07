package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.PasswordResetTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.PasswordResetTokenEntityMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Password Reset Token Persistence Adapter Tests")
class PasswordResetTokenPersistenceAdapterTest {

    private lateinit var passwordResetTokenPersistenceAdapter: PasswordResetTokenPersistenceAdapter
    private val passwordResetTokenRepository = mockk<PasswordResetTokenRepository>()
    private val passwordResetTokenEntityMapper = mockk<PasswordResetTokenEntityMapper>()

    @BeforeEach
    fun setUp() {
        passwordResetTokenPersistenceAdapter = PasswordResetTokenPersistenceAdapter(
            passwordResetTokenRepository,
            passwordResetTokenEntityMapper
        )
    }

    @Test
    @DisplayName("Should save password reset token domain when save is called")
    fun shouldSavePasswordResetTokenDomainWhenSaveIsCalled() {
        // Given
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 1L,
            token = "reset-token-123",
            expiryDate = Instant.now().plusSeconds(3600)
        )
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 1L,
            token = "reset-token-123",
            userId = 1L,
            expiryDate = Instant.now().plusSeconds(3600)
        )

        every { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) } returns passwordResetTokenEntity
        every { passwordResetTokenRepository.save(passwordResetTokenEntity) } returns passwordResetTokenEntity

        // When
        passwordResetTokenPersistenceAdapter.save(passwordResetTokenDom)

        // Then
        verify(exactly = 1) { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) }
        verify(exactly = 1) { passwordResetTokenRepository.save(passwordResetTokenEntity) }
    }

    @Test
    @DisplayName("Should find password reset token by token when token exists")
    fun shouldFindPasswordResetTokenByTokenWhenTokenExists() {
        // Given
        val token = "existing-reset-token-456"
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 2L,
            token = token,
            userId = 2L,
            expiryDate = Instant.now().plusSeconds(3600)
        )
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 2L,
            token = token,
            expiryDate = Instant.now().plusSeconds(3600)
        )

        every { passwordResetTokenRepository.findByToken(token) } returns passwordResetTokenEntity
        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns passwordResetTokenDom

        // When
        val result = passwordResetTokenPersistenceAdapter.findByToken(token)

        // Then
        assertNotNull(result)
        assertEquals(passwordResetTokenDom, result)
        verify(exactly = 1) { passwordResetTokenRepository.findByToken(token) }
        verify(exactly = 1) { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) }
    }

    @Test
    @DisplayName("Should return null when password reset token by token does not exist")
    fun shouldReturnNullWhenPasswordResetTokenByTokenDoesNotExist() {
        // Given
        val nonExistentToken = "nonexistent-reset-token"

        every { passwordResetTokenRepository.findByToken(nonExistentToken) } returns null

        // When
        val result = passwordResetTokenPersistenceAdapter.findByToken(nonExistentToken)

        // Then
        assertNull(result)
        verify(exactly = 1) { passwordResetTokenRepository.findByToken(nonExistentToken) }
        verify(exactly = 0) { passwordResetTokenEntityMapper.toDomain(any()) }
    }

    @Test
    @DisplayName("Should delete password reset token by token when delete by token is called")
    fun shouldDeletePasswordResetTokenByTokenWhenDeleteByTokenIsCalled() {
        // Given
        val tokenToDelete = "token-to-delete-789"

        every { passwordResetTokenRepository.deleteByToken(tokenToDelete) } returns Unit

        // When
        passwordResetTokenPersistenceAdapter.deleteByToken(tokenToDelete)

        // Then
        verify(exactly = 1) { passwordResetTokenRepository.deleteByToken(tokenToDelete) }
    }

    @Test
    @DisplayName("Should delete password reset token by user id when delete by user id is called")
    fun shouldDeletePasswordResetTokenByUserIdWhenDeleteByUserIdIsCalled() {
        // Given
        val userIdToDelete = 300L

        every { passwordResetTokenRepository.deleteByUserId(userIdToDelete) } returns Unit

        // When
        passwordResetTokenPersistenceAdapter.deleteByUserId(userIdToDelete)

        // Then
        verify(exactly = 1) { passwordResetTokenRepository.deleteByUserId(userIdToDelete) }
    }

    @Test
    @DisplayName("Should delegate save operation to repository and mapper correctly")
    fun shouldDelegateSaveOperationToRepositoryAndMapperCorrectly() {
        // Given
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 400L,
            token = "delegate-reset-token-abc",
            expiryDate = Instant.now().plusSeconds(7200)
        )
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 3L,
            token = "delegate-reset-token-abc",
            userId = 400L,
            expiryDate = Instant.now().plusSeconds(7200)
        )

        every { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) } returns passwordResetTokenEntity
        every { passwordResetTokenRepository.save(passwordResetTokenEntity) } returns passwordResetTokenEntity

        // When
        passwordResetTokenPersistenceAdapter.save(passwordResetTokenDom)

        // Then
        verify(exactly = 1) { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) }
        verify(exactly = 1) { passwordResetTokenRepository.save(passwordResetTokenEntity) }
    }

    @Test
    @DisplayName("Should handle used password reset token correctly")
    fun shouldHandleUsedPasswordResetTokenCorrectly() {
        // Given
        val usedToken = "used-reset-token-def"
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 4L,
            token = usedToken,
            userId = 500L,
            expiryDate = Instant.now().plusSeconds(3600)
        )
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 500L,
            token = usedToken,
            expiryDate = Instant.now().plusSeconds(3600)
        )

        every { passwordResetTokenRepository.findByToken(usedToken) } returns passwordResetTokenEntity
        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns passwordResetTokenDom

        // When
        val result = passwordResetTokenPersistenceAdapter.findByToken(usedToken)

        // Then
        assertNotNull(result)
        // Note: PasswordResetTokenEntity doesn't have isUsed field
        assertEquals(passwordResetTokenDom, result)
        verify(exactly = 1) { passwordResetTokenRepository.findByToken(usedToken) }
        verify(exactly = 1) { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) }
    }

    @Test
    @DisplayName("Should handle expired password reset token correctly")
    fun shouldHandleExpiredPasswordResetTokenCorrectly() {
        // Given
        val expiredToken = "expired-reset-token-ghi"
        val pastDate = Instant.now().minusSeconds(3600)
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 5L,
            token = expiredToken,
            userId = 600L,
            expiryDate = pastDate
        )
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 600L,
            token = expiredToken,
            expiryDate = pastDate
        )

        every { passwordResetTokenRepository.findByToken(expiredToken) } returns passwordResetTokenEntity
        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns passwordResetTokenDom

        // When
        val result = passwordResetTokenPersistenceAdapter.findByToken(expiredToken)

        // Then
        assertNotNull(result)
        assertEquals(pastDate, result.expiryDate)
        assertEquals(passwordResetTokenDom, result)
        verify(exactly = 1) { passwordResetTokenRepository.findByToken(expiredToken) }
        verify(exactly = 1) { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) }
    }

    @Test
    @DisplayName("Should handle multiple delete operations independently")
    fun shouldHandleMultipleDeleteOperationsIndependently() {
        // Given
        val token1 = "reset-token-delete-1"
        val token2 = "reset-token-delete-2"
        val userId1 = 700L
        val userId2 = 800L

        every { passwordResetTokenRepository.deleteByToken(token1) } returns Unit
        every { passwordResetTokenRepository.deleteByToken(token2) } returns Unit
        every { passwordResetTokenRepository.deleteByUserId(userId1) } returns Unit
        every { passwordResetTokenRepository.deleteByUserId(userId2) } returns Unit

        // When
        passwordResetTokenPersistenceAdapter.deleteByToken(token1)
        passwordResetTokenPersistenceAdapter.deleteByToken(token2)
        passwordResetTokenPersistenceAdapter.deleteByUserId(userId1)
        passwordResetTokenPersistenceAdapter.deleteByUserId(userId2)

        // Then
        verify(exactly = 1) { passwordResetTokenRepository.deleteByToken(token1) }
        verify(exactly = 1) { passwordResetTokenRepository.deleteByToken(token2) }
        verify(exactly = 1) { passwordResetTokenRepository.deleteByUserId(userId1) }
        verify(exactly = 1) { passwordResetTokenRepository.deleteByUserId(userId2) }
    }

    @Test
    @DisplayName("Should use method reference for domain mapping correctly")
    fun shouldUseMethodReferenceForDomainMappingCorrectly() {
        // Given
        val token = "method-ref-reset-token"
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 6L,
            token = token,
            userId = 6L,
            expiryDate = Instant.now().plusSeconds(3600)
        )
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 6L,
            token = token,
            expiryDate = Instant.now().plusSeconds(3600)
        )

        every { passwordResetTokenRepository.findByToken(token) } returns passwordResetTokenEntity
        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns passwordResetTokenDom

        // When
        val result = passwordResetTokenPersistenceAdapter.findByToken(token)

        // Then
        assertNotNull(result)
        assertEquals(passwordResetTokenDom, result)
        verify(exactly = 1) { passwordResetTokenRepository.findByToken(token) }
        verify(exactly = 1) { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) }
    }
}
