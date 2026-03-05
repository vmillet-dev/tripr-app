package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.enums.UserRoleEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoleEntityMapperTest {

    private val mapper: RoleEntityMapper = RoleEntityMapperImpl()

    @Test
    fun `toEntity should map string to RoleEntity`() {
        // given

        // when
        val result = mapper.toEntity("ROLE_ADMIN")

        // then
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(1L)
        assertThat(result.type).isEqualTo("ROLE_ADMIN")
    }

    @Test
    fun `fromEntity should map RoleEntity to string`() {
        // given
        val roleEntity = RoleEntity(UserRoleEnum.ROLE_USER.id, UserRoleEnum.ROLE_USER.type)

        // when
        val result = mapper.fromEntity(roleEntity)

        // then
        assertThat(result).isEqualTo("ROLE_USER")
    }
}
