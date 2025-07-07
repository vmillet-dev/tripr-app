package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RefreshTokenEntityMapper
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

@DisplayName("Refresh Token Persistence Adapter Tests")
class RefreshTokenPersistenceAdapterTest {

    private lateinit var refreshTokenPersistenceAdapter: RefreshTokenPersistenceAdapter
    private val refreshTokenRepository = mockk<RefreshTokenRepository>()
    private val refreshTokenEntityMapper = mockk<RefreshTokenEntityMapper>()

    @BeforeEach
    fun setUp() {
        refreshTokenPersistenceAdapter = RefreshTokenPersistenceAdapter(refreshTokenRepository, refreshTokenEntityMapper)
    }

    @Test
    @DisplayName("Should save refresh token domain when save is called")
    fun shouldSaveRefreshTokenDomainWhenSaveIsCalled() {
        // Given
        val refreshTokenDom = RefreshTokenDom(
            userId = 100L,
            token = "refresh-token-123",
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )
        val refreshTokenEntity = RefreshTokenEntity(
            id = 1L,
            userId = 100L,
            token = "refresh-token-123",
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )

        every { refreshTokenEntityMapper.toEntity(refreshTokenDom) } returns refreshTokenEntity
        every { refreshTokenRepository.save(refreshTokenEntity) } returns refreshTokenEntity

        // When
        refreshTokenPersistenceAdapter.save(refreshTokenDom)

        // Then
        verify(exactly = 1) { refreshTokenEntityMapper.toEntity(refreshTokenDom) }
        verify(exactly = 1) { refreshTokenRepository.save(refreshTokenEntity) }
    }

    @Test
    @DisplayName("Should find refresh token by token when token exists")
    fun shouldFindRefreshTokenByTokenWhenTokenExists() {
        // Given
        val token = "existing-token-456"
        val refreshTokenEntity = RefreshTokenEntity(
            id = 2L,
            userId = 200L,
            token = token,
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )
        val refreshTokenDom = RefreshTokenDom(
            userId = 200L,
            token = token,
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )

        every { refreshTokenRepository.findByToken(token) } returns refreshTokenEntity
        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns refreshTokenDom

        // When
        val result = refreshTokenPersistenceAdapter.findByToken(token)

        // Then
        assertNotNull(result)
        assertEquals(refreshTokenDom, result)
        verify(exactly = 1) { refreshTokenRepository.findByToken(token) }
        verify(exactly = 1) { refreshTokenEntityMapper.toDomain(refreshTokenEntity) }
    }

    @Test
    @DisplayName("Should return null when refresh token by token does not exist")
    fun shouldReturnNullWhenRefreshTokenByTokenDoesNotExist() {
        // Given
        val nonExistentToken = "nonexistent-token"

        every { refreshTokenRepository.findByToken(nonExistentToken) } returns null

        // When
        val result = refreshTokenPersistenceAdapter.findByToken(nonExistentToken)

        // Then
        assertNull(result)
        verify(exactly = 1) { refreshTokenRepository.findByToken(nonExistentToken) }
        verify(exactly = 0) { refreshTokenEntityMapper.toDomain(any()) }
    }

    @Test
    @DisplayName("Should delete refresh token by token when delete by token is called")
    fun shouldDeleteRefreshTokenByTokenWhenDeleteByTokenIsCalled() {
        // Given
        val tokenToDelete = "token-to-delete-789"

        every { refreshTokenRepository.deleteByToken(tokenToDelete) } returns Unit

        // When
        refreshTokenPersistenceAdapter.deleteByToken(tokenToDelete)

        // Then
        verify(exactly = 1) { refreshTokenRepository.deleteByToken(tokenToDelete) }
    }

    @Test
    @DisplayName("Should delete refresh token by user id when delete by user id is called")
    fun shouldDeleteRefreshTokenByUserIdWhenDeleteByUserIdIsCalled() {
        // Given
        val userIdToDelete = 300L

        every { refreshTokenRepository.deleteByUserId(userIdToDelete) } returns Unit

        // When
        refreshTokenPersistenceAdapter.deleteByUserId(userIdToDelete)

        // Then
        verify(exactly = 1) { refreshTokenRepository.deleteByUserId(userIdToDelete) }
    }

    @Test
    @DisplayName("Should delegate save operation to repository and mapper correctly")
    fun shouldDelegateSaveOperationToRepositoryAndMapperCorrectly() {
        // Given
        val refreshTokenDom = RefreshTokenDom(
            userId = 400L,
            token = "delegate-token-abc",
            expiryDate = Instant.now().plusSeconds(14 * 24 * 60 * 60),
            revoked = true
        )
        val refreshTokenEntity = RefreshTokenEntity(
            id = 3L,
            userId = 400L,
            token = "delegate-token-abc",
            expiryDate = Instant.now().plusSeconds(14 * 24 * 60 * 60),
            revoked = true
        )

        every { refreshTokenEntityMapper.toEntity(refreshTokenDom) } returns refreshTokenEntity
        every { refreshTokenRepository.save(refreshTokenEntity) } returns refreshTokenEntity

        // When
        refreshTokenPersistenceAdapter.save(refreshTokenDom)

        // Then
        verify(exactly = 1) { refreshTokenEntityMapper.toEntity(refreshTokenDom) }
        verify(exactly = 1) { refreshTokenRepository.save(refreshTokenEntity) }
    }

    @Test
    @DisplayName("Should handle revoked refresh token correctly")
    fun shouldHandleRevokedRefreshTokenCorrectly() {
        // Given
        val revokedToken = "revoked-token-def"
        val refreshTokenEntity = RefreshTokenEntity(
            id = 4L,
            userId = 500L,
            token = revokedToken,
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = true
        )
        val refreshTokenDom = RefreshTokenDom(
            userId = 500L,
            token = revokedToken,
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = true
        )

        every { refreshTokenRepository.findByToken(revokedToken) } returns refreshTokenEntity
        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns refreshTokenDom

        // When
        val result = refreshTokenPersistenceAdapter.findByToken(revokedToken)

        // Then
        assertNotNull(result)
        assertEquals(true, result.revoked)
        assertEquals(refreshTokenDom, result)
        verify(exactly = 1) { refreshTokenRepository.findByToken(revokedToken) }
        verify(exactly = 1) { refreshTokenEntityMapper.toDomain(refreshTokenEntity) }
    }

    @Test
    @DisplayName("Should handle expired refresh token correctly")
    fun shouldHandleExpiredRefreshTokenCorrectly() {
        // Given
        val expiredToken = "expired-token-ghi"
        val pastDate = Instant.now().minusSeconds(24 * 60 * 60)
        val refreshTokenEntity = RefreshTokenEntity(
            id = 5L,
            userId = 600L,
            token = expiredToken,
            expiryDate = pastDate,
            revoked = false
        )
        val refreshTokenDom = RefreshTokenDom(
            userId = 600L,
            token = expiredToken,
            expiryDate = pastDate,
            revoked = false
        )

        every { refreshTokenRepository.findByToken(expiredToken) } returns refreshTokenEntity
        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns refreshTokenDom

        // When
        val result = refreshTokenPersistenceAdapter.findByToken(expiredToken)

        // Then
        assertNotNull(result)
        assertEquals(pastDate, result.expiryDate)
        assertEquals(refreshTokenDom, result)
        verify(exactly = 1) { refreshTokenRepository.findByToken(expiredToken) }
        verify(exactly = 1) { refreshTokenEntityMapper.toDomain(refreshTokenEntity) }
    }

    @Test
    @DisplayName("Should handle multiple delete operations independently")
    fun shouldHandleMultipleDeleteOperationsIndependently() {
        // Given
        val token1 = "token-delete-1"
        val token2 = "token-delete-2"
        val userId1 = 700L
        val userId2 = 800L

        every { refreshTokenRepository.deleteByToken(token1) } returns Unit
        every { refreshTokenRepository.deleteByToken(token2) } returns Unit
        every { refreshTokenRepository.deleteByUserId(userId1) } returns Unit
        every { refreshTokenRepository.deleteByUserId(userId2) } returns Unit

        // When
        refreshTokenPersistenceAdapter.deleteByToken(token1)
        refreshTokenPersistenceAdapter.deleteByToken(token2)
        refreshTokenPersistenceAdapter.deleteByUserId(userId1)
        refreshTokenPersistenceAdapter.deleteByUserId(userId2)

        // Then
        verify(exactly = 1) { refreshTokenRepository.deleteByToken(token1) }
        verify(exactly = 1) { refreshTokenRepository.deleteByToken(token2) }
        verify(exactly = 1) { refreshTokenRepository.deleteByUserId(userId1) }
        verify(exactly = 1) { refreshTokenRepository.deleteByUserId(userId2) }
    }

    @Test
    @DisplayName("Should use method reference for domain mapping correctly")
    fun shouldUseMethodReferenceForDomainMappingCorrectly() {
        // Given
        val token = "method-ref-token"
        val refreshTokenEntity = RefreshTokenEntity(
            id = 6L,
            userId = 900L,
            token = token,
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )
        val refreshTokenDom = RefreshTokenDom(
            userId = 900L,
            token = token,
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )

        every { refreshTokenRepository.findByToken(token) } returns refreshTokenEntity
        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns refreshTokenDom

        // When
        val result = refreshTokenPersistenceAdapter.findByToken(token)

        // Then
        assertNotNull(result)
        assertEquals(refreshTokenDom, result)
        verify(exactly = 1) { refreshTokenRepository.findByToken(token) }
        verify(exactly = 1) { refreshTokenEntityMapper.toDomain(refreshTokenEntity) }
    }
}
