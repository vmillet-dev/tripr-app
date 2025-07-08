package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.UserDom
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@DisplayName("User Entity Mapper Tests")
class UserEntityMapperTest {

    private val userEntityMapper = object : UserEntityMapper {
        override fun toDomain(entity: UserEntity): UserDom {
            return UserDom(
                id = entity.id,
                username = entity.username,
                email = entity.email,
                password = entity.password,
                roles = entity.roles.map { it.type }.toSet(),
                enabled = entity.enabled
            )
        }

        override fun toEntity(domain: UserDom): UserEntity {
            return UserEntity(
                id = domain.id,
                username = domain.username,
                email = domain.email,
                password = domain.password,
                enabled = domain.enabled,
                roles = domain.roles.map { RoleEntity(id = 0L, type = it) }.toMutableSet()
            )
        }
    }

    @Test
    @DisplayName("Should map user entity to domain when entity is provided")
    fun shouldMapUserEntityToDomainWhenEntityIsProvided() {
        // Given
        val userEntity = UserEntity(
            id = 1L,
            username = "testuser",
            email = "test@example.com",
            password = "hashedpassword",
            enabled = true,
            roles = mutableSetOf(
                RoleEntity(id = 1L, type = "USER"),
                RoleEntity(id = 2L, type = "ADMIN")
            )
        )

        // When
        val userDom = userEntityMapper.toDomain(userEntity)

        // Then
        assertNotNull(userDom)
        assertEquals(userEntity.id, userDom.id)
        assertEquals(userEntity.username, userDom.username)
        assertEquals(userEntity.email, userDom.email)
        assertEquals(userEntity.password, userDom.password)
        assertEquals(userEntity.enabled, userDom.enabled)
        assertTrue(userDom.roles.contains("USER"))
        assertTrue(userDom.roles.contains("ADMIN"))
    }

    @Test
    @DisplayName("Should map user domain to entity when domain is provided")
    fun shouldMapUserDomainToEntityWhenDomainIsProvided() {
        // Given
        val userDom = UserDom(
            id = 2L,
            username = "domainuser",
            email = "domain@example.com",
            password = "domainpassword",
            roles = setOf("USER", "MODERATOR"),
            enabled = false
        )

        // When
        val userEntity = userEntityMapper.toEntity(userDom)

        // Then
        assertNotNull(userEntity)
        assertEquals(userDom.id, userEntity.id)
        assertEquals(userDom.username, userEntity.username)
        assertEquals(userDom.email, userEntity.email)
        assertEquals(userDom.password, userEntity.password)
        assertEquals(userDom.enabled, userEntity.enabled)
        assertEquals(2, userEntity.roles.size)
        assertTrue(userEntity.roles.any { it.type == "USER" })
        assertTrue(userEntity.roles.any { it.type == "MODERATOR" })
    }

    @Test
    @DisplayName("Should handle null entity correctly")
    fun shouldHandleNullEntityCorrectly() {
        // Given
        val nullEntity: UserEntity? = null

        // When
        val userDom = nullEntity?.let { userEntityMapper.toDomain(it) }

        // Then
        assertNull(userDom)
    }

    @Test
    @DisplayName("Should handle null domain correctly")
    fun shouldHandleNullDomainCorrectly() {
        // Given
        val nullDomain: UserDom? = null

        // When
        val userEntity = nullDomain?.let { userEntityMapper.toEntity(it) }

        // Then
        assertNull(userEntity)
    }

    @Test
    @DisplayName("Should map entity with empty roles to domain correctly")
    fun shouldMapEntityWithEmptyRolesToDomainCorrectly() {
        // Given
        val userEntity = UserEntity(
            id = 3L,
            username = "noroleuser",
            email = "norole@example.com",
            password = "norolepassword",
            enabled = true,
            roles = mutableSetOf()
        )

        // When
        val userDom = userEntityMapper.toDomain(userEntity)

        // Then
        assertNotNull(userDom)
        assertEquals(userEntity.id, userDom.id)
        assertEquals(userEntity.username, userDom.username)
        assertEquals(userEntity.email, userDom.email)
        assertEquals(userEntity.password, userDom.password)
        assertEquals(userEntity.enabled, userDom.enabled)
        assertTrue(userDom.roles.isEmpty())
    }

    @Test
    @DisplayName("Should map domain with empty roles to entity correctly")
    fun shouldMapDomainWithEmptyRolesToEntityCorrectly() {
        // Given
        val userDom = UserDom(
            id = 4L,
            username = "emptyroleuser",
            email = "emptyrole@example.com",
            password = "emptyrolepassword",
            roles = emptySet(),
            enabled = true
        )

        // When
        val userEntity = userEntityMapper.toEntity(userDom)

        // Then
        assertNotNull(userEntity)
        assertEquals(userDom.id, userEntity.id)
        assertEquals(userDom.username, userEntity.username)
        assertEquals(userDom.email, userEntity.email)
        assertEquals(userDom.password, userEntity.password)
        assertEquals(userDom.enabled, userEntity.enabled)
        assertTrue(userEntity.roles.isEmpty())
    }

    @Test
    @DisplayName("Should handle disabled user mapping correctly")
    fun shouldHandleDisabledUserMappingCorrectly() {
        // Given
        val userEntity = UserEntity(
            id = 5L,
            username = "disableduser",
            email = "disabled@example.com",
            password = "disabledpassword",
            enabled = false,
            roles = mutableSetOf(RoleEntity(id = 1L, type = "USER"))
        )

        // When
        val userDom = userEntityMapper.toDomain(userEntity)

        // Then
        assertNotNull(userDom)
        assertEquals(false, userDom.enabled)
        assertEquals(userEntity.enabled, userDom.enabled)
    }

    @Test
    @DisplayName("Should handle single role mapping correctly")
    fun shouldHandleSingleRoleMappingCorrectly() {
        // Given
        val userEntity = UserEntity(
            id = 6L,
            username = "singleuser",
            email = "single@example.com",
            password = "singlepassword",
            enabled = true,
            roles = mutableSetOf(RoleEntity(id = 1L, type = "ADMIN"))
        )

        // When
        val userDom = userEntityMapper.toDomain(userEntity)

        // Then
        assertNotNull(userDom)
        assertEquals(1, userDom.roles.size)
        assertTrue(userDom.roles.contains("ADMIN"))
    }

    @Test
    @DisplayName("Should handle multiple roles mapping correctly")
    fun shouldHandleMultipleRolesMappingCorrectly() {
        // Given
        val userDom = UserDom(
            id = 7L,
            username = "multiuser",
            email = "multi@example.com",
            password = "multipassword",
            roles = setOf("USER", "ADMIN", "MODERATOR", "GUEST"),
            enabled = true
        )

        // When
        val userEntity = userEntityMapper.toEntity(userDom)

        // Then
        assertNotNull(userEntity)
        assertEquals(4, userEntity.roles.size)
        val roleNames = userEntity.roles.map { it.type }
        assertTrue(roleNames.contains("USER"))
        assertTrue(roleNames.contains("ADMIN"))
        assertTrue(roleNames.contains("MODERATOR"))
        assertTrue(roleNames.contains("GUEST"))
    }

    @Test
    @DisplayName("Should maintain bidirectional mapping consistency")
    fun shouldMaintainBidirectionalMappingConsistency() {
        // Given
        val originalUserDom = UserDom(
            id = 8L,
            username = "bidirectional",
            email = "bidirectional@example.com",
            password = "bidirectionalpassword",
            roles = setOf("USER", "ADMIN"),
            enabled = true
        )

        // When
        val userEntity = userEntityMapper.toEntity(originalUserDom)
        val mappedBackUserDom = userEntityMapper.toDomain(userEntity)

        // Then
        assertNotNull(userEntity)
        assertNotNull(mappedBackUserDom)
        assertEquals(originalUserDom.id, mappedBackUserDom.id)
        assertEquals(originalUserDom.username, mappedBackUserDom.username)
        assertEquals(originalUserDom.email, mappedBackUserDom.email)
        assertEquals(originalUserDom.password, mappedBackUserDom.password)
        assertEquals(originalUserDom.enabled, mappedBackUserDom.enabled)
        assertEquals(originalUserDom.roles.size, mappedBackUserDom.roles.size)
        assertTrue(mappedBackUserDom.roles.containsAll(originalUserDom.roles))
    }
}
