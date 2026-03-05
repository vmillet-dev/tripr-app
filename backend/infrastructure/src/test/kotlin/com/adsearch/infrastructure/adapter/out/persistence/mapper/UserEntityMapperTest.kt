package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.enums.UserRoleEnum
import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserEntityMapperTest {

    private val mapper: UserEntityMapper = UserEntityMapperImpl().apply {
        val field = UserEntityMapperImpl::class.java.getDeclaredField("roleEntityMapper")
        field.isAccessible = true
        field.set(this, RoleEntityMapperImpl())
    }

    @Test
    fun `toDomain should map UserEntity to User`() {
        // given
        val roles = mutableSetOf(RoleEntity(UserRoleEnum.ROLE_USER.id, UserRoleEnum.ROLE_USER.type))
        val entity = UserEntity(1L, "john", "john@example.com", "hashed_pwd", true, roles)

        // when
        val domain = mapper.toDomain(entity)

        // then
        assertThat(domain.id).isEqualTo(1L)
        assertThat(domain.username).isEqualTo("john")
        assertThat(domain.email).isEqualTo("john@example.com")
        assertThat(domain.password).isEqualTo("hashed_pwd")
        assertThat(domain.enabled).isTrue()
        assertThat(domain.roles).contains("ROLE_USER")
    }

    @Test
    fun `toEntity should map User to UserEntity`() {
        // given
        val domain = User(2L, "jane", "jane@example.com", "pass", setOf(UserRoleEnum.ROLE_ADMIN.type), false)

        // when
        val entity = mapper.toEntity(domain)

        // then
        assertThat(entity.id).isEqualTo(2L)
        assertThat(entity.username).isEqualTo("jane")
        assertThat(entity.email).isEqualTo("jane@example.com")
        assertThat(entity.password).isEqualTo("pass")
        assertThat(entity.enabled).isFalse()
        assertThat(entity.roles.map { it.type }).contains("ROLE_ADMIN")
    }
}
