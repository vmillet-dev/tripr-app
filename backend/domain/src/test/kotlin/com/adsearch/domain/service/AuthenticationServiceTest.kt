package com.adsearch.domain.service

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.enums.UserRoleEnum
import com.adsearch.domain.exception.InvalidCredentialsException
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.`in`.LoginUserUseCase
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
        val user = User(10, username, "john@mail.com", "hpass", setOf(UserRoleEnum.ROLE_USER.type), true)
        every { authenticationProvider.authenticate(username, pwd) } returns username
        every { userPersistence.findByUsername(username) } returns user
        every { configurationProvider.getRefreshTokenExpiration() } returns 3600L
        every { tokenGenerator.generateAccessToken(user) } returns "access-token"

        // When
        val resp: LoginUserUseCase.LoginUser = service.login(LoginUserUseCase.LoginUserCommand(username, pwd))

        // Then
        assertThat(resp.accessToken).isEqualTo("access-token")
        assertThat(resp.refreshToken).isNotBlank()

        verifyOrder {
            authenticationProvider.authenticate("john", "pwd")
            userPersistence.findByUsername("john")
            tokenPersistence.save(withArg { rt: RefreshToken ->
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

        assertThatThrownBy { service.login(LoginUserUseCase.LoginUserCommand(username, pwd)) }
            .isInstanceOf(InvalidCredentialsException::class.java)
            .hasMessageContaining("Authentication failed for user $username")
    }

    @Test
    fun `logout should delete token when provided`() {
        val token = "rt-token"
        service.logout(token)
        verify {
            tokenPersistence.deleteTokenAndType(
                "dcf752bf51d5062c0f24312ec8002c370371def660cb3a3406c2eba9cc30da0affd44d42c6e7d8a541a4940174cb19113c4e17d8e042b533d701e443fdcff360",
                TokenTypeEnum.REFRESH
            )
        }
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

        every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns null
        assertThatThrownBy { service.refreshAccessToken("missing") }
            .isInstanceOf(InvalidTokenException::class.java)
    }

    @Test
    fun `refreshAccessToken should throw TokenExpiredException when token expired or revoked`() {
        val rt = RefreshToken(5, "t", Instant.now().minusSeconds(10), false)
        every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns rt

        assertThatThrownBy { service.refreshAccessToken("t") }
            .isInstanceOf(TokenExpiredException::class.java)

        // revoked case
        val rt2 = RefreshToken(5, "t2", Instant.now().plusSeconds(1000), true)
        every { tokenPersistence.findByTokenAndType("t2", TokenTypeEnum.REFRESH) } returns rt2
        assertThatThrownBy { service.refreshAccessToken("t2") }
            .isInstanceOf(TokenExpiredException::class.java)
        verify {
            tokenPersistence.deleteTokenAndType(
                "99f97d455d5d62b24f3a942a1abc3fa8863fc0ce2037f52f09bd785b22b800d4f2e7b2b614cb600ffc2a4fe24679845b24886d69bb776fcfa46e54d188889c6f",
                TokenTypeEnum.REFRESH
            )
        }
        verify {
            tokenPersistence.deleteTokenAndType(
                "dbabd9bd5c26b441bf9cd7c07b82b9974d9a71e1379253b9f644e7554287e2d155eb369e081e7ad2cf1594fdae4f6b0385260376f44f20b01ca0a8c05b32fafc",
                TokenTypeEnum.REFRESH
            )
        }
    }

    @Test
    fun `refreshAccessToken should throw UserNotFoundException when user not found`() {
        val rt = RefreshToken(99, "t3", Instant.now().plusSeconds(1000), false)
        every {
            tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH)
        } returns rt
        every { userPersistence.findById(99) } returns null

        assertThatThrownBy { service.refreshAccessToken("t3") }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `refreshAccessToken should return new access token when valid`() {
        val user = User(20, "alice", "a@a.com", "p", setOf(UserRoleEnum.ROLE_USER.type), true)
        val rt = RefreshToken(user.id, "good", Instant.now().plusSeconds(1000), false)
        every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns rt
        every { userPersistence.findById(user.id) } returns user
        every { tokenGenerator.generateAccessToken(user) } returns "new-access"

        val resp = service.refreshAccessToken("good")
        assertThat(resp.accessToken).isEqualTo("new-access")
    }
}
