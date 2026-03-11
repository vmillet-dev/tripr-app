package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.TokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.TokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.TokenEntityMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class TokenPersistenceAdapterTest {

    private val tokenRepository = mockk<TokenRepository>()
    private val tokenEntityMapper = mockk<TokenEntityMapper>()
    private val adapter = TokenPersistenceAdapter(tokenRepository, tokenEntityMapper)

    @Test
    fun `save should map domain to entity and save in repository`() {
        // given
        val domain = RefreshToken(1L, "token", Instant.now(), false)
        val entity = mockk<TokenEntity>()
        every { tokenEntityMapper.toEntity(domain) } returns entity
        every { tokenRepository.save(entity) } returns entity

        // when
        adapter.save(domain)

        // then
        verify { tokenRepository.save(entity) }
    }

    @Test
    fun `deleteTokenAndType should call repository`() {
        // given
        every { tokenRepository.deleteByTokenAndType("some-token", TokenTypeEnum.REFRESH) } returns Unit

        // when
        adapter.deleteTokenAndType("some-token", TokenTypeEnum.REFRESH)

        // then
        verify { tokenRepository.deleteByTokenAndType("some-token", TokenTypeEnum.REFRESH) }
    }

    @Test
    fun `findByTokenAndType should return domain when entity exists`() {
        // given
        val entity = mockk<TokenEntity>()
        val domain = RefreshToken(1L, "some-token", Instant.now(), false)

        every { tokenRepository.findByTokenAndType("some-token", TokenTypeEnum.REFRESH) } returns entity
        every { tokenEntityMapper.toDomain(entity) } returns domain

        // when
        val result = adapter.findByTokenAndType("some-token", TokenTypeEnum.REFRESH)

        // then
        assertThat(result).isEqualTo(domain)
    }

    @Test
    fun `findByTokenAndType should return null when entity not found`() {
        // given
        every { tokenRepository.findByTokenAndType(any(), any()) } returns null

        // when
        val result = adapter.findByTokenAndType("missing", TokenTypeEnum.REFRESH)

        // then
        assertThat(result).isNull()
    }
}
