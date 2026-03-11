package com.adsearch.domain.service

import com.adsearch.domain.enums.TokenTypeEnum
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
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.Instant

class AuthenticationServiceTest {

    private val authenticationProvider = mockk<AuthenticationProviderPort>()
    private val tokenGenerator = mockk<TokenGeneratorPort>()
    private val configurationProvider = mockk<ConfigurationProviderPort>()
    private val tokenPersistence = mockk<TokenPersistencePort>(relaxed = true)
    private val userPersistence = mockk<UserPersistencePort>()

    private lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setUp() {
        authenticationService = AuthenticationService(
            authenticationProvider,
            tokenGenerator,
            configurationProvider,
            tokenPersistence,
            userPersistence
        )
    }

    @Nested
    inner class LoginTests {
        @Test
        fun `login should return tokens and save refresh token when credentials are valid`() {
            // given
            val user = User(1, "user", "user@example.com", "hashed", emptySet(), true)

            every { authenticationProvider.authenticate("user", "password") } returns "user"
            every { userPersistence.findByUsername("user") } returns user
            every { configurationProvider.getRefreshTokenExpiration() } returns 3600L
            every { tokenGenerator.generateAccessToken(user) } returns "access-token"
            every { tokenPersistence.save(any()) } returns Unit

            // when
            val result = authenticationService.login(LoginUserUseCase.LoginUserCommand("user", "password"))

            // then
            assertThat(result.accessToken).isEqualTo("access-token")
            assertThat(result.refreshToken).isNotEmpty()

            verify {
                tokenPersistence.save(withArg { refreshToken ->
                    assertThat(refreshToken.userId).isEqualTo(1L)
                    assertThat(refreshToken.expiryDate).isAfter(Instant.now())
                    assertThat(refreshToken.revoked).isFalse()
                })
            }
        }

        @Test
        fun `login should throw InvalidCredentialsException when authentication fails`() {
            // given
            every { authenticationProvider.authenticate(any(), any()) } throws RuntimeException("Auth failed")

            // when / then
            assertThatThrownBy { authenticationService.login(LoginUserUseCase.LoginUserCommand("user", "wrong-password")) }
                .isInstanceOf(InvalidCredentialsException::class.java)
                .hasMessageContaining("Authentication failed for user user")
        }
    }

    @Nested
    inner class LogoutTests {
        @Test
        fun `logout should delete refresh token when token is provided`() {
            // given

            // when
            authenticationService.logout("refresh-token")

            // then
            verify { tokenPersistence.deleteTokenAndType(any(), TokenTypeEnum.REFRESH) }
        }

        @Test
        fun `logout should throw InvalidTokenException when token is null`() {
            // when / then
            assertThatThrownBy { authenticationService.logout(null) }
                .isInstanceOf(InvalidTokenException::class.java)
                .hasMessageContaining("Logout attempted without refresh token")
        }
    }

    @Nested
    inner class RefreshTokenTests {
        @Test
        fun `refreshAccessToken should return new access token when refresh token is valid`() {
            // given
            val refreshToken = RefreshToken(1L, "hashed-token", Instant.now().plusSeconds(3600), false)
            val user = User(1, "user", "user@example.com", "hashed", emptySet(), true)

            every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns refreshToken
            every { userPersistence.findById(1L) } returns user
            every { tokenGenerator.generateAccessToken(user) } returns "new-access-token"

            // when
            val result = authenticationService.refreshAccessToken("valid-refresh-token")

            // then
            assertThat(result.accessToken).isEqualTo("new-access-token")
        }

        @Test
        fun `refreshAccessToken should throw InvalidTokenException when token is null`() {
            // when / then
            assertThatThrownBy { authenticationService.refreshAccessToken(null) }
                .isInstanceOf(InvalidTokenException::class.java)
                .hasMessageContaining("Token refresh failed - refresh token missing")
        }

        @Test
        fun `refreshAccessToken should throw InvalidTokenException when refresh token is not found`() {
            // given
            every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns null

            // when / then
            assertThatThrownBy { authenticationService.refreshAccessToken("unknown-token") }
                .isInstanceOf(InvalidTokenException::class.java)
                .hasMessageContaining("invalid refresh token provided")
        }

        @ParameterizedTest
        @ValueSource(strings = ["expired", "revoked"])
        fun `refreshAccessToken should throw TokenExpiredException when token is expired or revoked`(reason: String) {
            // given
            val expiry = if (reason == "expired") Instant.now().minusSeconds(60) else Instant.now().plusSeconds(3600)
            val revoked = reason == "revoked"
            val refreshToken = RefreshToken(1L, "hashed-token", expiry, revoked)

            every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns refreshToken

            // when / then
            assertThatThrownBy { authenticationService.refreshAccessToken("some-token") }
                .isInstanceOf(TokenExpiredException::class.java)
                .hasMessageContaining("token expired or revoked")

            verify { tokenPersistence.deleteTokenAndType(any(), TokenTypeEnum.REFRESH) }
        }

        @Test
        fun `refreshAccessToken should throw UserNotFoundException when user no longer exists`() {
            // given
            val refreshToken = RefreshToken(1L, "hashed-token", Instant.now().plusSeconds(3600), false)
            every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.REFRESH) } returns refreshToken
            every { userPersistence.findById(1L) } returns null

            // when / then
            assertThatThrownBy { authenticationService.refreshAccessToken("valid-token") }
                .isInstanceOf(UserNotFoundException::class.java)
                .hasMessageContaining("user not found with user id: 1")
        }
    }
}
