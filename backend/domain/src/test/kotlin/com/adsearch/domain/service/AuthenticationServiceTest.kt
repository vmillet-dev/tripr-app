package com.adsearch.domain.service

import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.model.auth.AuthResponse
import com.adsearch.domain.model.command.LoginUserCommand
import com.adsearch.domain.model.enums.TokenTypeEnum
import com.adsearch.domain.model.enums.UserRoleEnum
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.authentication.AuthenticationProviderPort
import com.adsearch.domain.port.out.authentication.TokenGeneratorPort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class AuthenticationServiceTest {

    private val authenticationProvider = mockk<AuthenticationProviderPort>(relaxed = true)
    private val tokenGenerator = mockk<TokenGeneratorPort>(relaxed = true)
    private val configurationProvider = mockk<ConfigurationProviderPort>(relaxed = true)
    private val tokenPersistence = mockk<TokenPersistencePort>(relaxed = true)
    private val userPersistence = mockk<UserPersistencePort>(relaxed = true)

    private val service = AuthenticationService(
        authenticationProvider,
        tokenGenerator,
        configurationProvider,
        tokenPersistence,
        userPersistence
    )

    @Test
    fun `login should return tokens when credentials valid`() {
        // Given
        val username = "john"
        val pwd = "pwd"
        val user = UserDom(10, username, "john@mail.com", "hpass", setOf(UserRoleEnum.ROLE_USER.type), true)
        every { authenticationProvider.authenticate(username, pwd) } returns username
        every { userPersistence.findByUsername(username) } returns user
        every { configurationProvider.getRefreshTokenExpiration() } returns 3600L
        every { tokenGenerator.generateAccessToken(user) } returns "access-token"

        // When
        val resp: AuthResponse = service.login(LoginUserCommand(username, pwd))

        // Then
        assertThat(resp.accessToken).isEqualTo("access-token")
        assertThat(resp.refreshToken).isNotBlank()

        verifyOrder {
            authenticationProvider.authenticate(username, pwd)
            userPersistence.findByUsername(username)
            tokenPersistence.deleteByUserId(user.id, TokenTypeEnum.REFRESH)
            tokenPersistence.save(withArg { rt: RefreshTokenDom ->
                assertThat(rt.userId).isEqualTo(user.id)
                assertThat(rt.token).isNotBlank()
                assertThat(rt.expiryDate).isAfterOrEqualTo(Instant.now())
            })
            tokenGenerator.generateAccessToken(user)
        }
    }

    @Test
    fun `login should throw InvalidCredentialsException when authentication fails`() {
        val username = "john"
        val pwd = "bad"
        every { authenticationProvider.authenticate(username, pwd) } throws RuntimeException("bad creds")

        assertThatThrownBy { service.login(LoginUserCommand(username, pwd)) }
            .isInstanceOf(InvalidCredentialsException::class.java)
            .hasMessageContaining("Authentication failed for user $username")
    }

    @Test
    fun `logout should delete token when provided`() {
        val token = "rt-token"
        val rt = RefreshTokenDom(1, token, Instant.now().plusSeconds(100), false)
        every { tokenPersistence.findByToken(token, TokenTypeEnum.REFRESH) } returns rt
        service.logout(token)
        verify { tokenPersistence.delete(rt) }
    }

    @Test
    fun `logout should throw InvalidTokenException when token is null`() {
        assertThatThrownBy { service.logout(null) }
            .isInstanceOf(InvalidTokenException::class.java)
            .hasMessageContaining("Logout attempted without refresh token")
    }

    @Test
    fun `refreshAccessToken should throw InvalidTokenException when token missing or invalid`() {
        assertThatThrownBy { service.refreshAccessToken(null) }
            .isInstanceOf(InvalidTokenException::class.java)

        every { tokenPersistence.findByToken("missing", TokenTypeEnum.REFRESH) } returns null
        assertThatThrownBy { service.refreshAccessToken("missing") }
            .isInstanceOf(InvalidTokenException::class.java)
    }

    @Test
    fun `refreshAccessToken should throw TokenExpiredException when token expired or revoked`() {
        val rt = RefreshTokenDom(5, "t", Instant.now().minusSeconds(10), false)
        every { tokenPersistence.findByToken("t", TokenTypeEnum.REFRESH) } returns rt

        assertThatThrownBy { service.refreshAccessToken("t") }
            .isInstanceOf(TokenExpiredException::class.java)

        // revoked case
        val rt2 = RefreshTokenDom(5, "t2", Instant.now().plusSeconds(1000), true)
        every { tokenPersistence.findByToken("t2", TokenTypeEnum.REFRESH) } returns rt2
        assertThatThrownBy { service.refreshAccessToken("t2") }
            .isInstanceOf(TokenExpiredException::class.java)
        verify { tokenPersistence.delete(rt) }
        verify { tokenPersistence.delete(rt2) }
    }

    @Test
    fun `refreshAccessToken should throw UserNotFoundException when user not found`() {
        val rt = RefreshTokenDom(99, "t3", Instant.now().plusSeconds(1000), false)
        every { tokenPersistence.findByToken("t3", TokenTypeEnum.REFRESH) } returns rt
        every { userPersistence.findById(99) } returns null

        assertThatThrownBy { service.refreshAccessToken("t3") }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `refreshAccessToken should return new access token when valid`() {
        val user = UserDom(20, "alice", "a@a.com", "p", setOf(UserRoleEnum.ROLE_USER.type), true)
        val rt = RefreshTokenDom(user.id, "good", Instant.now().plusSeconds(1000), false)
        every { tokenPersistence.findByToken("good", TokenTypeEnum.REFRESH) } returns rt
        every { userPersistence.findById(user.id) } returns user
        every { tokenGenerator.generateAccessToken(user) } returns "new-access"

        val resp = service.refreshAccessToken("good")
        assertThat(resp.accessToken).isEqualTo("new-access")
    }
}
