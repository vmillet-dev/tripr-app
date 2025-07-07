package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Refresh Token Entity Mapper Tests")
class RefreshTokenEntityMapperTest {

    private val refreshTokenEntityMapper = mockk<RefreshTokenEntityMapper>()

    @Test
    @DisplayName("Should map refresh token entity to domain when entity is provided")
    fun shouldMapRefreshTokenEntityToDomainWhenEntityIsProvided() {
        // Given
        val expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60)
        val refreshTokenEntity = RefreshTokenEntity(
            id = 1L,
            token = "refresh-token-123",
            userId = 100L,
            expiryDate = expiryDate,
            revoked = false
        )
        val expectedRefreshTokenDom = RefreshTokenDom(
            userId = 100L,
            token = "refresh-token-123",
            expiryDate = expiryDate,
            revoked = false
        )

        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns expectedRefreshTokenDom

        // When
        val refreshTokenDom = refreshTokenEntityMapper.toDomain(refreshTokenEntity)

        // Then
        assertNotNull(refreshTokenDom)
        assertEquals(expectedRefreshTokenDom.token, refreshTokenDom.token)
        assertEquals(expectedRefreshTokenDom.userId, refreshTokenDom.userId)
        assertEquals(expectedRefreshTokenDom.expiryDate, refreshTokenDom.expiryDate)
        assertEquals(expectedRefreshTokenDom.revoked, refreshTokenDom.revoked)
    }

    @Test
    @DisplayName("Should map refresh token domain to entity when domain is provided")
    fun shouldMapRefreshTokenDomainToEntityWhenDomainIsProvided() {
        // Given
        val expiryDate = Instant.now().plusSeconds(14 * 24 * 60 * 60)
        val refreshTokenDom = RefreshTokenDom(
            userId = 200L,
            token = "domain-refresh-token-456",
            expiryDate = expiryDate,
            revoked = true
        )
        val expectedRefreshTokenEntity = RefreshTokenEntity(
            id = 1L,
            userId = 200L,
            token = "domain-refresh-token-456",
            expiryDate = expiryDate,
            revoked = true
        )

        every { refreshTokenEntityMapper.toEntity(refreshTokenDom) } returns expectedRefreshTokenEntity

        // When
        val refreshTokenEntity = refreshTokenEntityMapper.toEntity(refreshTokenDom)

        // Then
        assertNotNull(refreshTokenEntity)
        assertEquals(expectedRefreshTokenEntity.token, refreshTokenEntity.token)
        assertEquals(expectedRefreshTokenEntity.userId, refreshTokenEntity.userId)
        assertEquals(expectedRefreshTokenEntity.expiryDate, refreshTokenEntity.expiryDate)
        assertEquals(expectedRefreshTokenEntity.revoked, refreshTokenEntity.revoked)
    }

    @Test
    @DisplayName("Should handle null entity correctly")
    fun shouldHandleNullEntityCorrectly() {
        // Given
        val nullEntity: RefreshTokenEntity? = null


        // When
        val refreshTokenDom = nullEntity?.let { refreshTokenEntityMapper.toDomain(it) }

        // Then
        assertNull(refreshTokenDom)
    }

    @Test
    @DisplayName("Should handle null domain correctly")
    fun shouldHandleNullDomainCorrectly() {
        // Given
        val nullDomain: RefreshTokenDom? = null


        // When
        val refreshTokenEntity = nullDomain?.let { refreshTokenEntityMapper.toEntity(it) }

        // Then
        assertNull(refreshTokenEntity)
    }

    @Test
    @DisplayName("Should map revoked refresh token correctly")
    fun shouldMapRevokedRefreshTokenCorrectly() {
        // Given
        val expiryDate = Instant.now().plusSeconds(24 * 60 * 60)
        val refreshTokenEntity = RefreshTokenEntity(
            id = 3L,
            token = "revoked-token-789",
            userId = 300L,
            expiryDate = expiryDate,
            revoked = true
        )
        val expectedRefreshTokenDom = RefreshTokenDom(
            userId = 300L,
            token = "revoked-token-789",
            expiryDate = expiryDate,
            revoked = true
        )

        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns expectedRefreshTokenDom

        // When
        val refreshTokenDom = refreshTokenEntityMapper.toDomain(refreshTokenEntity)

        // Then
        assertNotNull(refreshTokenDom)
        assertEquals(true, refreshTokenDom.revoked)
        assertEquals(expectedRefreshTokenDom.revoked, refreshTokenDom.revoked)
    }

    @Test
    @DisplayName("Should map expired refresh token correctly")
    fun shouldMapExpiredRefreshTokenCorrectly() {
        // Given
        val pastDate = Instant.now().minusSeconds(24 * 60 * 60)
        val refreshTokenEntity = RefreshTokenEntity(
            id = 4L,
            token = "expired-token-abc",
            userId = 400L,
            expiryDate = pastDate,
            revoked = false
        )
        val expectedRefreshTokenDom = RefreshTokenDom(
            userId = 400L,
            token = "expired-token-abc",
            expiryDate = pastDate,
            revoked = false
        )

        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns expectedRefreshTokenDom

        // When
        val refreshTokenDom = refreshTokenEntityMapper.toDomain(refreshTokenEntity)

        // Then
        assertNotNull(refreshTokenDom)
        assertEquals(pastDate, refreshTokenDom.expiryDate)
        assertEquals(expectedRefreshTokenDom.expiryDate, refreshTokenDom.expiryDate)
    }

    @Test
    @DisplayName("Should handle long token strings correctly")
    fun shouldHandleLongTokenStringsCorrectly() {
        // Given
        val longToken = "very-long-refresh-token-with-many-characters-and-numbers-123456789-abcdefghijklmnopqrstuvwxyz"
        val expiryDate = Instant.now().plusSeconds(30 * 24 * 60 * 60)
        val refreshTokenEntity = RefreshTokenEntity(
            id = 5L,
            token = longToken,
            userId = 500L,
            expiryDate = expiryDate,
            revoked = false
        )
        val expectedRefreshTokenDom = RefreshTokenDom(
            userId = 500L,
            token = longToken,
            expiryDate = expiryDate,
            revoked = false
        )

        every { refreshTokenEntityMapper.toDomain(refreshTokenEntity) } returns expectedRefreshTokenDom

        // When
        val refreshTokenDom = refreshTokenEntityMapper.toDomain(refreshTokenEntity)

        // Then
        assertNotNull(refreshTokenDom)
        assertEquals(longToken, refreshTokenDom.token)
        assertEquals(expectedRefreshTokenDom.token, refreshTokenDom.token)
    }

    @Test
    @DisplayName("Should handle different user IDs correctly")
    fun shouldHandleDifferentUserIdsCorrectly() {
        // Given
        val expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60)
        val refreshTokenDom = RefreshTokenDom(
            userId = 999L,
            token = "user-specific-token",
            expiryDate = expiryDate,
            revoked = false
        )
        val expectedRefreshTokenEntity = RefreshTokenEntity(
            id = 1L,
            userId = 999L,
            token = "user-specific-token",
            expiryDate = expiryDate,
            revoked = false
        )

        every { refreshTokenEntityMapper.toEntity(refreshTokenDom) } returns expectedRefreshTokenEntity

        // When
        val refreshTokenEntity = refreshTokenEntityMapper.toEntity(refreshTokenDom)

        // Then
        assertNotNull(refreshTokenEntity)
        assertEquals(999L, refreshTokenEntity.userId)
        assertEquals(expectedRefreshTokenEntity.userId, refreshTokenEntity.userId)
    }

    @Test
    @DisplayName("Should handle future expiry dates correctly")
    fun shouldHandleFutureExpiryDatesCorrectly() {
        // Given
        val futureDate = Instant.now().plusSeconds(365 * 24 * 60 * 60)
        val refreshTokenDom = RefreshTokenDom(
            userId = 700L,
            token = "future-expiry-token",
            expiryDate = futureDate,
            revoked = false
        )
        val expectedRefreshTokenEntity = RefreshTokenEntity(
            id = 1L,
            userId = 700L,
            token = "future-expiry-token",
            expiryDate = futureDate,
            revoked = false
        )

        every { refreshTokenEntityMapper.toEntity(refreshTokenDom) } returns expectedRefreshTokenEntity

        // When
        val refreshTokenEntity = refreshTokenEntityMapper.toEntity(refreshTokenDom)

        // Then
        assertNotNull(refreshTokenEntity)
        assertEquals(futureDate, refreshTokenEntity.expiryDate)
        assertEquals(expectedRefreshTokenEntity.expiryDate, refreshTokenEntity.expiryDate)
    }

    @Test
    @DisplayName("Should maintain bidirectional mapping consistency")
    fun shouldMaintainBidirectionalMappingConsistency() {
        // Given
        val originalRefreshTokenDom = RefreshTokenDom(
            userId = 800L,
            token = "bidirectional-token",
            expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60),
            revoked = false
        )
        val expectedRefreshTokenEntity = RefreshTokenEntity(
            id = 1L,
            userId = 800L,
            token = "bidirectional-token",
            expiryDate = originalRefreshTokenDom.expiryDate,
            revoked = false
        )

        every { refreshTokenEntityMapper.toEntity(originalRefreshTokenDom) } returns expectedRefreshTokenEntity
        every { refreshTokenEntityMapper.toDomain(expectedRefreshTokenEntity) } returns originalRefreshTokenDom

        // When
        val refreshTokenEntity = refreshTokenEntityMapper.toEntity(originalRefreshTokenDom)
        val mappedBackRefreshTokenDom = refreshTokenEntityMapper.toDomain(refreshTokenEntity)

        // Then
        assertNotNull(refreshTokenEntity)
        assertNotNull(mappedBackRefreshTokenDom)
        assertEquals(originalRefreshTokenDom.token, mappedBackRefreshTokenDom.token)
        assertEquals(originalRefreshTokenDom.userId, mappedBackRefreshTokenDom.userId)
        assertEquals(originalRefreshTokenDom.expiryDate, mappedBackRefreshTokenDom.expiryDate)
        assertEquals(originalRefreshTokenDom.revoked, mappedBackRefreshTokenDom.revoked)
    }
}
