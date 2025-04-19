package com.adsearch.integration.util

import com.adsearch.domain.model.RoleType
import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.mapper.PasswordResetTokenEntityMapper
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

/**
 * Helper class for creating test data in integration tests
 * Uses JPA entities directly to ensure schema compatibility
 */
@Component
class TestDataHelper(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val userEntityMapper: UserEntityMapper,
    @Autowired private val passwordResetTokenEntityMapper: PasswordResetTokenEntityMapper
) {

    /**
     * Create a test user in the database
     */
    @Transactional
    fun createTestUser(
        username: String = "testuser",
        password: String = "password",
        roles: List<String> = listOf("USER")
    ): User {
        val encodedPassword = passwordEncoder.encode(password)

        // Create role entities if they don't exist
        val roleEntities = roles.map { roleName ->
            val roleType = RoleType.valueOf(roleName)
            val existingRole = entityManager
                .createQuery("SELECT r FROM RoleEntity r WHERE r.name = :name", RoleEntity::class.java)
                .setParameter("name", roleType)
                .resultList.firstOrNull()
            
            existingRole ?: RoleEntity(name = roleType).also {
                entityManager.persist(it)
                entityManager.flush()
            }
        }.toMutableSet()

        // Create and persist user entity
        val userEntity = UserEntity(
            username = username,
            password = encodedPassword,
            roles = roleEntities
        )

        entityManager.persist(userEntity)
        entityManager.flush()

        return userEntityMapper.toDomain(userEntity)
    }

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

    /**
     * Clean up test data
     */
    @Transactional
    fun cleanupTestData() {
        // Use native queries to clean up all test data
        // Clean up in reverse order to avoid foreign key constraints
        entityManager.createNativeQuery("DELETE FROM password_reset_tokens").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM refresh_tokens").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM user_roles").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM roles").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate()
    }
}
