package com.adsearch.infrastructure.adapter.out.persistence.mapper

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.RefreshToken
import com.adsearch.infrastructure.adapter.out.persistence.entity.TokenEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class TokenEntityMapperTest {

    private val mapper: TokenEntityMapper = TokenEntityMapperImpl()

    @Test
    fun `toDomain should map REFRESH token entity to RefreshToken domain`() {
        // given
        val expiry = Instant.now().plusSeconds(3600)
        val entity = TokenEntity(1L, 10L, "token-value", expiry, TokenTypeEnum.REFRESH, false)

        // when
        val domain = mapper.toDomain(entity)

        // then
        assertThat(domain).isInstanceOf(RefreshToken::class.java)
        assertThat(domain.userId).isEqualTo(10L)
        assertThat(domain.token).isEqualTo("token-value")
        assertThat(domain.expiryDate).isEqualTo(expiry)
        assertThat(domain.type).isEqualTo(TokenTypeEnum.REFRESH)
        assertThat(domain.revoked).isFalse()
    }

    @Test
    fun `toDomain should map PASSWORD_RESET token entity to PasswordResetToken domain`() {
        // given
        val expiry = Instant.now().plusSeconds(600)
        val entity = TokenEntity(2L, 20L, "reset-token", expiry, TokenTypeEnum.PASSWORD_RESET, true)

        // when
        val domain = mapper.toDomain(entity)

        // then
        assertThat(domain).isInstanceOf(PasswordResetToken::class.java)
        assertThat(domain.userId).isEqualTo(20L)
        assertThat(domain.token).isEqualTo("reset-token")
        assertThat(domain.expiryDate).isEqualTo(expiry)
        assertThat(domain.type).isEqualTo(TokenTypeEnum.PASSWORD_RESET)
        assertThat(domain.revoked).isTrue()
    }

    @Test
    fun `toEntity should map domain Token to TokenEntity`() {
        // given
        val expiry = Instant.now().plusSeconds(3600)
        val domain = RefreshToken(15L, "refresh-val", expiry, true)

        // when
        val entity = mapper.toEntity(domain)

        // then
        assertThat(entity.userId).isEqualTo(15L)
        assertThat(entity.token).isEqualTo("refresh-val")
        assertThat(entity.expiryDate).isEqualTo(expiry)
        assertThat(entity.type).isEqualTo(TokenTypeEnum.REFRESH)
        assertThat(entity.revoked).isTrue()
    }
}
