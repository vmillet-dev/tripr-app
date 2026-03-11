package com.adsearch.infrastructure.security

import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JwtUserDetailsServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val userDetailsService = JwtUserDetailsService(userRepository)

    @Test
    fun `loadUserByUsername should return UserPrincipal when user found`() {
        // given
        val userEntity = UserEntity(1L, "testuser", "test@e.com", "pass", true, mutableSetOf())
        every { userRepository.findByUsername("testuser") } returns userEntity

        // when
        val result = userDetailsService.loadUserByUsername("testuser")

        // then
        assertThat(result.username).isEqualTo("testuser")
        assertThat(result.isEnabled).isTrue()
    }

    @Test
    fun `loadUserByUsername should throw UsernameNotFoundException when user not found`() {
        // given
        every { userRepository.findByUsername("unknown") } returns null

        // when / then
        assertThatThrownBy { userDetailsService.loadUserByUsername("unknown") }
            .isInstanceOf(UsernameNotFoundException::class.java)
            .hasMessageContaining("unknown")
    }
}
