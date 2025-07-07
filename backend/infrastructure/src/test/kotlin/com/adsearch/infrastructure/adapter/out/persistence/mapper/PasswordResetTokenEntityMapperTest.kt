package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Password Reset Token Entity Mapper Tests")
class PasswordResetTokenEntityMapperTest {

    private val passwordResetTokenEntityMapper = mockk<PasswordResetTokenEntityMapper>()

    @Test
    @DisplayName("Should map password reset token entity to domain when entity is provided")
    fun shouldMapPasswordResetTokenEntityToDomainWhenEntityIsProvided() {
        // Given
        val expiryDate = Instant.now().plusSeconds(3600)
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 1L,
            token = "reset-token-123",
            userId = 100L,
            expiryDate = expiryDate
        )
        val expectedPasswordResetTokenDom = PasswordResetTokenDom(
            userId = 100L,
            token = "reset-token-123",
            expiryDate = expiryDate
        )

        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns expectedPasswordResetTokenDom

        // When
        val passwordResetTokenDom = passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity)

        // Then
        assertNotNull(passwordResetTokenDom)
        assertEquals(expectedPasswordResetTokenDom.token, passwordResetTokenDom.token)
        assertEquals(expectedPasswordResetTokenDom.userId, passwordResetTokenDom.userId)
        assertEquals(expectedPasswordResetTokenDom.expiryDate, passwordResetTokenDom.expiryDate)
    }

    @Test
    @DisplayName("Should map password reset token domain to entity when domain is provided")
    fun shouldMapPasswordResetTokenDomainToEntityWhenDomainIsProvided() {
        // Given
        val expiryDate = Instant.now().plusSeconds(7200)
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 200L,
            token = "domain-reset-token-456",
            expiryDate = expiryDate
        )
        val expectedPasswordResetTokenEntity = PasswordResetTokenEntity(
            id = 1L,
            userId = 200L,
            token = "domain-reset-token-456",
            expiryDate = expiryDate
        )

        every { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) } returns expectedPasswordResetTokenEntity

        // When
        val passwordResetTokenEntity = passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom)

        // Then
        assertNotNull(passwordResetTokenEntity)
        assertEquals(expectedPasswordResetTokenEntity.token, passwordResetTokenEntity.token)
        assertEquals(expectedPasswordResetTokenEntity.userId, passwordResetTokenEntity.userId)
        assertEquals(expectedPasswordResetTokenEntity.expiryDate, passwordResetTokenEntity.expiryDate)
    }

    @Test
    @DisplayName("Should handle null entity correctly")
    fun shouldHandleNullEntityCorrectly() {
        // Given
        val nullEntity: PasswordResetTokenEntity? = null


        // When
        val passwordResetTokenDom = nullEntity?.let { passwordResetTokenEntityMapper.toDomain(it) }

        // Then
        assertNull(passwordResetTokenDom)
    }

    @Test
    @DisplayName("Should handle null domain correctly")
    fun shouldHandleNullDomainCorrectly() {
        // Given
        val nullDomain: PasswordResetTokenDom? = null


        // When
        val passwordResetTokenEntity = nullDomain?.let { passwordResetTokenEntityMapper.toEntity(it) }

        // Then
        assertNull(passwordResetTokenEntity)
    }

    @Test
    @DisplayName("Should map used password reset token correctly")
    fun shouldMapUsedPasswordResetTokenCorrectly() {
        // Given
        val expiryDate = Instant.now().plusSeconds(1800)
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 3L,
            token = "used-token-789",
            userId = 300L,
            expiryDate = expiryDate
        )
        val expectedPasswordResetTokenDom = PasswordResetTokenDom(
            userId = 300L,
            token = "used-token-789",
            expiryDate = expiryDate
        )

        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns expectedPasswordResetTokenDom

        // When
        val passwordResetTokenDom = passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity)

        // Then
        assertNotNull(passwordResetTokenDom)
        assertEquals(expectedPasswordResetTokenDom.token, passwordResetTokenDom.token)
        assertEquals(expectedPasswordResetTokenDom.userId, passwordResetTokenDom.userId)
    }

    @Test
    @DisplayName("Should map expired password reset token correctly")
    fun shouldMapExpiredPasswordResetTokenCorrectly() {
        // Given
        val pastDate = Instant.now().minusSeconds(3600)
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 4L,
            token = "expired-token-abc",
            userId = 400L,
            expiryDate = pastDate
        )
        val expectedPasswordResetTokenDom = PasswordResetTokenDom(
            userId = 400L,
            token = "expired-token-abc",
            expiryDate = pastDate
        )

        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns expectedPasswordResetTokenDom

        // When
        val passwordResetTokenDom = passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity)

        // Then
        assertNotNull(passwordResetTokenDom)
        assertEquals(pastDate, passwordResetTokenDom.expiryDate)
        assertEquals(expectedPasswordResetTokenDom.expiryDate, passwordResetTokenDom.expiryDate)
    }

    @Test
    @DisplayName("Should handle long token strings correctly")
    fun shouldHandleLongTokenStringsCorrectly() {
        // Given
        val longToken = "very-long-password-reset-token-with-many-characters-and-numbers-123456789-abcdefghijklmnopqrstuvwxyz"
        val expiryDate = Instant.now().plusSeconds(86400)
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 5L,
            token = longToken,
            userId = 500L,
            expiryDate = expiryDate
        )
        val expectedPasswordResetTokenDom = PasswordResetTokenDom(
            userId = 500L,
            token = longToken,
            expiryDate = expiryDate
        )

        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns expectedPasswordResetTokenDom

        // When
        val passwordResetTokenDom = passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity)

        // Then
        assertNotNull(passwordResetTokenDom)
        assertEquals(longToken, passwordResetTokenDom.token)
        assertEquals(expectedPasswordResetTokenDom.token, passwordResetTokenDom.token)
    }

    @Test
    @DisplayName("Should handle different user IDs correctly")
    fun shouldHandleDifferentUserIdsCorrectly() {
        // Given
        val expiryDate = Instant.now().plusSeconds(3600)
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 999L,
            token = "user-specific-reset-token",
            expiryDate = expiryDate
        )
        val expectedPasswordResetTokenEntity = PasswordResetTokenEntity(
            id = 1L,
            userId = 999L,
            token = "user-specific-reset-token",
            expiryDate = expiryDate
        )

        every { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) } returns expectedPasswordResetTokenEntity

        // When
        val passwordResetTokenEntity = passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom)

        // Then
        assertNotNull(passwordResetTokenEntity)
        assertEquals(999L, passwordResetTokenEntity.userId)
        assertEquals(expectedPasswordResetTokenEntity.userId, passwordResetTokenEntity.userId)
    }

    @Test
    @DisplayName("Should handle short expiry periods correctly")
    fun shouldHandleShortExpiryPeriodsCorrectly() {
        // Given
        val shortExpiryDate = Instant.now().plusSeconds(300)
        val passwordResetTokenDom = PasswordResetTokenDom(
            userId = 700L,
            token = "short-expiry-token",
            expiryDate = shortExpiryDate
        )
        val expectedPasswordResetTokenEntity = PasswordResetTokenEntity(
            id = 1L,
            userId = 700L,
            token = "short-expiry-token",
            expiryDate = shortExpiryDate
        )

        every { passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom) } returns expectedPasswordResetTokenEntity

        // When
        val passwordResetTokenEntity = passwordResetTokenEntityMapper.toEntity(passwordResetTokenDom)

        // Then
        assertNotNull(passwordResetTokenEntity)
        assertEquals(shortExpiryDate, passwordResetTokenEntity.expiryDate)
        assertEquals(expectedPasswordResetTokenEntity.expiryDate, passwordResetTokenEntity.expiryDate)
    }

    @Test
    @DisplayName("Should handle special characters in token correctly")
    fun shouldHandleSpecialCharactersInTokenCorrectly() {
        // Given
        val tokenWithSpecialChars = "reset-token-with-special-chars_123!@#$%"
        val expiryDate = Instant.now().plusSeconds(3600)
        val passwordResetTokenEntity = PasswordResetTokenEntity(
            id = 8L,
            token = tokenWithSpecialChars,
            userId = 800L,
            expiryDate = expiryDate
        )
        val expectedPasswordResetTokenDom = PasswordResetTokenDom(
            userId = 800L,
            token = tokenWithSpecialChars,
            expiryDate = expiryDate
        )

        every { passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity) } returns expectedPasswordResetTokenDom

        // When
        val passwordResetTokenDom = passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity)

        // Then
        assertNotNull(passwordResetTokenDom)
        assertEquals(tokenWithSpecialChars, passwordResetTokenDom.token)
        assertEquals(expectedPasswordResetTokenDom.token, passwordResetTokenDom.token)
    }

    @Test
    @DisplayName("Should maintain bidirectional mapping consistency")
    fun shouldMaintainBidirectionalMappingConsistency() {
        // Given
        val originalPasswordResetTokenDom = PasswordResetTokenDom(
            userId = 900L,
            token = "bidirectional-reset-token",
            expiryDate = Instant.now().plusSeconds(3600)
        )
        val expectedPasswordResetTokenEntity = PasswordResetTokenEntity(
            id = 1L,
            userId = 900L,
            token = "bidirectional-reset-token",
            expiryDate = originalPasswordResetTokenDom.expiryDate
        )

        every { passwordResetTokenEntityMapper.toEntity(originalPasswordResetTokenDom) } returns expectedPasswordResetTokenEntity
        every { passwordResetTokenEntityMapper.toDomain(expectedPasswordResetTokenEntity) } returns originalPasswordResetTokenDom

        // When
        val passwordResetTokenEntity = passwordResetTokenEntityMapper.toEntity(originalPasswordResetTokenDom)
        val mappedBackPasswordResetTokenDom = passwordResetTokenEntityMapper.toDomain(passwordResetTokenEntity)

        // Then
        assertNotNull(passwordResetTokenEntity)
        assertNotNull(mappedBackPasswordResetTokenDom)
        assertEquals(originalPasswordResetTokenDom.token, mappedBackPasswordResetTokenDom.token)
        assertEquals(originalPasswordResetTokenDom.userId, mappedBackPasswordResetTokenDom.userId)
        assertEquals(originalPasswordResetTokenDom.expiryDate, mappedBackPasswordResetTokenDom.expiryDate)
    }
}
