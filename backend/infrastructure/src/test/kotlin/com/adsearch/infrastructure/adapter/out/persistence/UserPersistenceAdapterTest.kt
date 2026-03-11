package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional

class UserPersistenceAdapterTest {

    private val userRepository = mockk<UserRepository>()
    private val userEntityMapper = mockk<UserEntityMapper>()
    private val adapter = UserPersistenceAdapter(userRepository, userEntityMapper)

    @Test
    fun `save should map domain to entity and save in repository`() {
        // given
        val user = User(1L, "u", "e", "p", emptySet(), true)
        val entity = mockk<UserEntity>()
        every { userEntityMapper.toEntity(user) } returns entity
        every { userRepository.save(entity) } returns entity

        // when
        adapter.save(user)

        // then
        verify { userRepository.save(entity) }
    }

    @Test
    fun `findByUsername should return domain user when entity found`() {
        // given
        val entity = mockk<UserEntity>()
        val user = User(1L, "john", "e", "p", emptySet(), true)
        every { userRepository.findByUsername("john") } returns entity
        every { userEntityMapper.toDomain(entity) } returns user

        // when
        val result = adapter.findByUsername("john")

        // then
        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `findById should return domain user when entity found`() {
        // given
        val entity = mockk<UserEntity>()
        val user = User(1L, "u", "e", "p", emptySet(), true)
        every { userRepository.findById(1L) } returns Optional.of(entity)
        every { userEntityMapper.toDomain(entity) } returns user

        // when
        val result = adapter.findById(1L)

        // then
        assertThat(result).isEqualTo(user)
    }

    @Test
    fun `findByEmail should return domain user when entity found`() {
        // given
        val entity = mockk<UserEntity>()
        val user = User(1L, "u", "john@e.com", "p", emptySet(), true)
        every { userRepository.findByEmail("john@e.com") } returns entity
        every { userEntityMapper.toDomain(entity) } returns user

        // when
        val result = adapter.findByEmail("john@e.com")

        // then
        assertThat(result).isEqualTo(user)
    }
}
