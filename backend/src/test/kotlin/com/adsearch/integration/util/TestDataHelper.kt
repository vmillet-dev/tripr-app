package com.adsearch.integration.util

import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

/**
 * Helper class for creating test data in integration tests
 * Uses JPA entities directly to ensure schema compatibility
 */
@Component
class TestDataHelper(
    private val entityManager: EntityManager
) {
    /**
     * Create a password reset token for a user
     */
    @Transactional
    fun createPasswordResetToken(
        userId: Long,
        token: String = UUID.randomUUID().toString(),
        expiryDate: Instant = Instant.now().plusSeconds(86400)
    ): String {
        // Create and persist password reset token entity
        val passwordResetToken = PasswordResetTokenEntity(
            userId = userId,
            token = token,
            expiryDate = expiryDate,
            used = false
        )

        entityManager.persist(passwordResetToken)
        entityManager.flush()

        return token
    }
}
