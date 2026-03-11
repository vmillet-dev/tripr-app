package com.adsearch.infrastructure.adapter.out.authentication

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication

class AuthenticationProviderAdapterTest {

    private val authenticationManager = mockk<AuthenticationManager>()
    private val adapter = AuthenticationProviderAdapter(authenticationManager)

    @Test
    fun `authenticate should return username when authentication is successful`() {
        // given
        val authentication = mockk<Authentication>()
        every { authentication.name } returns "user1"
        every { authenticationManager.authenticate(any<UsernamePasswordAuthenticationToken>()) } returns authentication

        // when
        val result = adapter.authenticate("user1", "password1")

        // then
        assertThat(result).isEqualTo("user1")
    }
}
