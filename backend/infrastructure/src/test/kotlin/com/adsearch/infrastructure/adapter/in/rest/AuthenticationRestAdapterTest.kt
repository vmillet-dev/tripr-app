package com.adsearch.infrastructure.adapter.`in`.rest

import com.adsearch.domain.port.`in`.CreateUserUseCase
import com.adsearch.domain.port.`in`.LoginUserUseCase
import com.adsearch.domain.port.`in`.LogoutUserUseCase
import com.adsearch.domain.port.`in`.PasswordResetUseCase
import com.adsearch.domain.port.`in`.RefreshTokenUseCase
import com.adsearch.infrastructure.adapter.`in`.rest.dto.AuthRequestDto
import com.adsearch.infrastructure.adapter.`in`.rest.dto.PasswordResetDto
import com.adsearch.infrastructure.adapter.`in`.rest.dto.PasswordResetRequestDto
import com.adsearch.infrastructure.adapter.`in`.rest.dto.RegisterRequestDto
import com.adsearch.infrastructure.adapter.`in`.rest.utils.ServletRequestUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus

class AuthenticationRestAdapterTest {

    private val loginUserUseCase = mockk<LoginUserUseCase>()
    private val createUserUseCase = mockk<CreateUserUseCase>()
    private val logoutUserUseCase = mockk<LogoutUserUseCase>()
    private val refreshTokenUseCase = mockk<RefreshTokenUseCase>()
    private val passwordResetUseCase = mockk<PasswordResetUseCase>()
    private val cookieName = "refresh_token"
    private val cookieMaxAge = 3600

    private lateinit var adapter: AuthenticationRestAdapter

    private val mockRequest = mockk<HttpServletRequest>()
    private val mockResponse = mockk<HttpServletResponse>()

    @BeforeEach
    fun setUp() {
        adapter = AuthenticationRestAdapter(
            loginUserUseCase,
            createUserUseCase,
            logoutUserUseCase,
            refreshTokenUseCase,
            passwordResetUseCase,
            cookieName,
            cookieMaxAge
        )
        mockkObject(ServletRequestUtils)
        every { ServletRequestUtils.currentRequest() } returns mockRequest
        every { ServletRequestUtils.currentResponse() } returns mockResponse
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(ServletRequestUtils)
    }

    @Nested
    inner class Login {
        @Test
        fun `login should return access token and set refresh token cookie`() {
            // given
            val authRequestDto = AuthRequestDto("john", "password")
            val loginUser = LoginUserUseCase.LoginUser("access-token-123", "refresh-token-456")
            every { loginUserUseCase.login(any()) } returns loginUser
            every { mockResponse.addHeader(HttpHeaders.SET_COOKIE, any()) } returns Unit

            // when
            val response = adapter.login(authRequestDto)

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.accessToken).isEqualTo("access-token-123")
            verify {
                loginUserUseCase.login(withArg {
                    assertThat(it.username).isEqualTo("john")
                    assertThat(it.password).isEqualTo("password")
                })
                mockResponse.addHeader(HttpHeaders.SET_COOKIE, match {
                    it.contains("refresh_token=refresh-token-456") &&
                        it.contains("Max-Age=3600") &&
                        it.contains("HttpOnly") &&
                        it.contains("Secure") &&
                        it.contains("SameSite=Strict")
                })
            }
        }
    }

    @Nested
    inner class Register {
        @Test
        fun `register should call createUserUseCase and return success message`() {
            // given
            val registerRequestDto = RegisterRequestDto("john", "password", "john@example.com")
            every { createUserUseCase.createUser(any()) } returns Unit

            // when
            val response = adapter.register(registerRequestDto)

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.message).isEqualTo("UserEntity registered successfully")
            verify {
                createUserUseCase.createUser(withArg {
                    assertThat(it.username).isEqualTo("john")
                    assertThat(it.email).isEqualTo("john@example.com")
                    assertThat(it.password).isEqualTo("password")
                })
            }
        }
    }

    @Nested
    inner class RefreshToken {
        @Test
        fun `refreshToken should return new access token when cookie is present`() {
            // given
            val cookie = Cookie("refresh_token", "valid-refresh-token")
            every { mockRequest.cookies } returns arrayOf(cookie)
            val accessToken = RefreshTokenUseCase.AccessToken("new-access-token")
            every { refreshTokenUseCase.refreshAccessToken("valid-refresh-token") } returns accessToken

            // when
            val response = adapter.refreshToken()

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.accessToken).isEqualTo("new-access-token")
            verify { refreshTokenUseCase.refreshAccessToken("valid-refresh-token") }
        }

        @Test
        fun `refreshToken should call use case with null when cookie is missing`() {
            // given
            every { mockRequest.cookies } returns null
            val accessToken = RefreshTokenUseCase.AccessToken("new-access-token")
            every { refreshTokenUseCase.refreshAccessToken(null) } returns accessToken

            // when
            val response = adapter.refreshToken()

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.accessToken).isEqualTo("new-access-token")
            verify { refreshTokenUseCase.refreshAccessToken(null) }
        }
    }

    @Nested
    inner class Logout {
        @Test
        fun `logout should call use case and clear cookie`() {
            // given
            val cookie = Cookie("refresh_token", "token-to-logout")
            every { mockRequest.cookies } returns arrayOf(cookie)
            every { logoutUserUseCase.logout("token-to-logout") } returns Unit
            every { mockResponse.addHeader(HttpHeaders.SET_COOKIE, any()) } returns Unit

            // when
            val response = adapter.logout()

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.message).isEqualTo("Logged out successfully")
            verify {
                logoutUserUseCase.logout("token-to-logout")
                mockResponse.addHeader(HttpHeaders.SET_COOKIE, match {
                    it.contains("refresh_token=") && it.contains("Max-Age=0")
                })
            }
        }
    }

    @Nested
    inner class RequestPasswordReset {
        @Test
        fun `requestPasswordReset should call use case and return success message`() {
            // given
            val requestDto = PasswordResetRequestDto("john")
            every { passwordResetUseCase.requestPasswordReset("john") } returns Unit

            // when
            val response = adapter.requestPasswordReset(requestDto)

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.message).isEqualTo("If the username exists, a password reset email has been sent")
            verify { passwordResetUseCase.requestPasswordReset("john") }
        }
    }

    @Nested
    inner class ResetPassword {
        @Test
        fun `resetPassword should call use case and return success message`() {
            // given
            val resetDto = PasswordResetDto("reset-token", "new-password")
            every { passwordResetUseCase.resetPassword("reset-token", "new-password") } returns Unit

            // when
            val response = adapter.resetPassword(resetDto)

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.message).isEqualTo("Password has been reset successfully")
            verify { passwordResetUseCase.resetPassword("reset-token", "new-password") }
        }
    }

    @Nested
    inner class ValidateToken {
        @Test
        fun `validateToken should return validity status`() {
            // given
            every { passwordResetUseCase.validateToken("some-token") } returns true

            // when
            val response = adapter.validateToken("some-token")

            // then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body?.valid).isTrue()
            verify { passwordResetUseCase.validateToken("some-token") }
        }
    }
}
