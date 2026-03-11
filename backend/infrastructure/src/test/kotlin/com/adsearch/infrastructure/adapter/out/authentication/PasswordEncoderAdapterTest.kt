package com.adsearch.infrastructure.adapter.out.authentication

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

class PasswordEncoderAdapterTest {

    private val passwordEncoder = mockk<PasswordEncoder>()
    private val adapter = PasswordEncoderAdapter(passwordEncoder)

    @Test
    fun `encode should delegate to Spring PasswordEncoder`() {
        // given
        every { passwordEncoder.encode("secret") } returns "encoded_secret"

        // when
        val result = adapter.encode("secret")

        // then
        assertThat(result).isEqualTo("encoded_secret")
    }
}
