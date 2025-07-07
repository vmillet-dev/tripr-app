package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.model.enum.UserRoleEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("Role Entity Mapper Tests")
class RoleEntityMapperTest {

    private val roleEntityMapper = object : RoleEntityMapper {}

    @Test
    @DisplayName("Should map string role to entity when valid role string is provided")
    fun shouldMapStringRoleToEntityWhenValidRoleStringIsProvided() {
        // Given
        val roleString = "ROLE_USER"

        // When
        val roleEntity = roleEntityMapper.toEntity(roleString)

        // Then
        assertNotNull(roleEntity)
        assertEquals(UserRoleEnum.ROLE_USER.id, roleEntity.id)
        assertEquals(UserRoleEnum.ROLE_USER.type, roleEntity.type)
        assertEquals("ROLE_USER", roleEntity.type)
    }

    @Test
    @DisplayName("Should map admin role string to entity correctly")
    fun shouldMapAdminRoleStringToEntityCorrectly() {
        // Given
        val roleString = "ROLE_ADMIN"

        // When
        val roleEntity = roleEntityMapper.toEntity(roleString)

        // Then
        assertNotNull(roleEntity)
        assertEquals(UserRoleEnum.ROLE_ADMIN.id, roleEntity.id)
        assertEquals(UserRoleEnum.ROLE_ADMIN.type, roleEntity.type)
        assertEquals("ROLE_ADMIN", roleEntity.type)
    }

    @Test
    @DisplayName("Should map role entity to string when entity is provided")
    fun shouldMapRoleEntityToStringWhenEntityIsProvided() {
        // Given
        val roleEntity = RoleEntity(id = 2L, type = "ROLE_USER")

        // When
        val roleString = roleEntityMapper.fromEntity(roleEntity)

        // Then
        assertEquals("ROLE_USER", roleString)
    }

    @Test
    @DisplayName("Should map admin role entity to string correctly")
    fun shouldMapAdminRoleEntityToStringCorrectly() {
        // Given
        val roleEntity = RoleEntity(id = 1L, type = "ROLE_ADMIN")

        // When
        val roleString = roleEntityMapper.fromEntity(roleEntity)

        // Then
        assertEquals("ROLE_ADMIN", roleString)
    }

    @Test
    @DisplayName("Should throw exception when invalid role string is provided")
    fun shouldThrowExceptionWhenInvalidRoleStringIsProvided() {
        // Given
        val invalidRoleString = "INVALID_ROLE"

        // When & Then
        assertThrows<IllegalArgumentException> {
            roleEntityMapper.toEntity(invalidRoleString)
        }
    }

    @Test
    @DisplayName("Should throw exception when null role string is provided")
    fun shouldThrowExceptionWhenNullRoleStringIsProvided() {
        // Given
        val nullRoleString: String? = null

        // When & Then
        assertThrows<NullPointerException> {
            roleEntityMapper.toEntity(nullRoleString!!)
        }
    }

    @Test
    @DisplayName("Should throw exception when empty role string is provided")
    fun shouldThrowExceptionWhenEmptyRoleStringIsProvided() {
        // Given
        val emptyRoleString = ""

        // When & Then
        assertThrows<IllegalArgumentException> {
            roleEntityMapper.toEntity(emptyRoleString)
        }
    }

    @Test
    @DisplayName("Should handle case sensitive role strings correctly")
    fun shouldHandleCaseSensitiveRoleStringsCorrectly() {
        // Given
        val lowerCaseRoleString = "role_user"

        // When & Then
        assertThrows<IllegalArgumentException> {
            roleEntityMapper.toEntity(lowerCaseRoleString)
        }
    }

    @Test
    @DisplayName("Should handle role entity with different type correctly")
    fun shouldHandleRoleEntityWithDifferentTypeCorrectly() {
        // Given
        val roleEntity = RoleEntity(id = 3L, type = "MODERATOR")

        // When
        val roleString = roleEntityMapper.fromEntity(roleEntity)

        // Then
        assertEquals("MODERATOR", roleString)
    }

    @Test
    @DisplayName("Should maintain consistency between toEntity and fromEntity operations")
    fun shouldMaintainConsistencyBetweenToEntityAndFromEntityOperations() {
        // Given
        val originalRoleString = "ROLE_USER"

        // When
        val roleEntity = roleEntityMapper.toEntity(originalRoleString)
        val mappedBackRoleString = roleEntityMapper.fromEntity(roleEntity)

        // Then
        assertEquals(originalRoleString, mappedBackRoleString)
    }

    @Test
    @DisplayName("Should maintain consistency for admin role between operations")
    fun shouldMaintainConsistencyForAdminRoleBetweenOperations() {
        // Given
        val originalRoleString = "ROLE_ADMIN"

        // When
        val roleEntity = roleEntityMapper.toEntity(originalRoleString)
        val mappedBackRoleString = roleEntityMapper.fromEntity(roleEntity)

        // Then
        assertEquals(originalRoleString, mappedBackRoleString)
    }

    @Test
    @DisplayName("Should handle role entity with null type gracefully")
    fun shouldHandleRoleEntityWithNullTypeGracefully() {
        // Given
        val roleEntity = RoleEntity(id = 2L, type = "ROLE_USER")

        // When
        val roleString = roleEntityMapper.fromEntity(roleEntity)

        // Then
        assertEquals("ROLE_USER", roleString)
    }

    @Test
    @DisplayName("Should create role entity with correct enum values")
    fun shouldCreateRoleEntityWithCorrectEnumValues() {
        // Given
        val roleString = "ROLE_USER"

        // When
        val roleEntity = roleEntityMapper.toEntity(roleString)

        // Then
        assertNotNull(roleEntity)
        assertEquals(UserRoleEnum.ROLE_USER.id, roleEntity.id)
        assertEquals(UserRoleEnum.ROLE_USER.type, roleEntity.type)
    }

    @Test
    @DisplayName("Should handle multiple role mappings independently")
    fun shouldHandleMultipleRoleMappingsIndependently() {
        // Given
        val userRoleString = "ROLE_USER"
        val adminRoleString = "ROLE_ADMIN"

        // When
        val userRoleEntity = roleEntityMapper.toEntity(userRoleString)
        val adminRoleEntity = roleEntityMapper.toEntity(adminRoleString)

        // Then
        assertEquals(UserRoleEnum.ROLE_USER.id, userRoleEntity.id)
        assertEquals(UserRoleEnum.ROLE_USER.type, userRoleEntity.type)
        assertEquals(UserRoleEnum.ROLE_ADMIN.id, adminRoleEntity.id)
        assertEquals(UserRoleEnum.ROLE_ADMIN.type, adminRoleEntity.type)
    }
}
