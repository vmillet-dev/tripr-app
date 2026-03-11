package com.adsearch.infrastructure.adapter.out.authentication

import com.adsearch.domain.model.User
import com.auth0.jwt.JWT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class JwtTokenGeneratorAdapterTest {

    private val adapter = JwtTokenGeneratorAdapter("mySecret", 3600L, "myIssuer")

    @Test
    fun `generateAccessToken should create a valid JWT token`() {
        // given
        val user = User(1L, "user1", "user1@e.com", "pass", setOf("ROLE_USER"), true)

        // when
        val token = adapter.generateAccessToken(user)

        // then
        assertThat(token).isNotEmpty()
        val decoded = JWT.decode(token)
        assertThat(decoded.subject).isEqualTo("user1")
        assertThat(decoded.issuer).isEqualTo("myIssuer")
        assertThat(decoded.getClaim("roles").asList(String::class.java)).containsExactly("ROLE_USER")
        assertThat(decoded.expiresAtAsInstant).isAfter(Instant.now())
    }

    @Test
    fun `validateAccessTokenAndGetUsername should return username for valid token`() {
        // given
        val user = User(1L, "user1", "user1@e.com", "pass", setOf("ROLE_USER"), true)
        val token = adapter.generateAccessToken(user)

        // when
        val username = adapter.validateAccessTokenAndGetUsername(token)

        // then
        assertThat(username).isEqualTo("user1")
    }

    @Test
    fun `validateAccessTokenAndGetUsername should return null for invalid token`() {
        // given
        val invalidToken = "invalid-token"

        // when
        val username = adapter.validateAccessTokenAndGetUsername(invalidToken)

        // then
        assertThat(username).isNull()
    }

    @Test
    fun `getAuthoritiesFromToken should return roles for valid token`() {
        // given
        val user = User(1L, "user1", "user1@e.com", "pass", setOf("ROLE_ADMIN", "ROLE_USER"), true)
        val token = adapter.generateAccessToken(user)

        // when
        val roles = adapter.getAuthoritiesFromToken(token)

        // then
        assertThat(roles).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER")
    }
}
