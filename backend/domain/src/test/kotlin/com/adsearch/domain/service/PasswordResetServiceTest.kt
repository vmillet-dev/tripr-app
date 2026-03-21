package com.adsearch.domain.service

import com.adsearch.domain.enums.TokenTypeEnum
import com.adsearch.domain.enums.UserRoleEnum
import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.model.User
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.authentication.PasswordEncoderPort
import com.adsearch.domain.port.out.notification.EmailServicePort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import com.adsearch.domain.port.out.persistence.deletePasswordResetTokenByUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

class PasswordResetServiceTest {

    private val configurationProvider = mockk<ConfigurationProviderPort>()
    private val emailService = mockk<EmailServicePort>(relaxed = true)
    private val passwordEncoder = mockk<PasswordEncoderPort>()
    private val userPersistence = mockk<UserPersistencePort>(relaxed = true)
    private val tokenPersistence = mockk<TokenPersistencePort>(relaxed = true)

    private lateinit var passwordResetService: PasswordResetService

    @BeforeEach
    fun setUp() {
        passwordResetService = PasswordResetService(
            configurationProvider,
            emailService,
            passwordEncoder,
            userPersistence,
            tokenPersistence
        )
    }

    @Nested
    inner class RequestPasswordReset {
        @Test
        fun `requestPasswordReset should save token and send email when user exists`() {
            // given
            val user = User(7, "bob", "bob@e.com", "p", setOf(UserRoleEnum.ROLE_USER.type), true)
            every { userPersistence.findByUsername("bob") } returns user
            every { configurationProvider.getPasswordResetTokenExpiration() } returns 3600L

            // when
            passwordResetService.requestPasswordReset("bob")

            // then
            verify { tokenPersistence.deleteByUserAndType(user, TokenTypeEnum.PASSWORD_RESET) }
            verify { tokenPersistence.save(any<PasswordResetToken>()) }
            verify { emailService.sendPasswordResetEmail("bob@e.com", any()) }
        }

        @Test
        fun `requestPasswordReset should throw UserNotFoundException when user missing`() {
            every { userPersistence.findByUsername("no") } returns null
            assertDoesNotThrow { passwordResetService.requestPasswordReset("no") }

            verify(exactly = 0) { tokenPersistence.deletePasswordResetTokenByUser(any()) }
            verify(exactly = 0) { tokenPersistence.save(any()) }
            verify(exactly = 0) { emailService.sendPasswordResetEmail(any(), any()) }
        }
    }

    @Nested
    inner class ResetPassword {
        @Test
        fun `resetPassword should update password when token valid`() {
            // given
            val user = User(8, "c", "c@c.com", "old", setOf(UserRoleEnum.ROLE_USER.type), true)
            val tokenDom = PasswordResetToken(user.id, "valid-token", Instant.now().plusSeconds(1000))

            every { tokenPersistence.findByTokenAndType("valid-token", TokenTypeEnum.PASSWORD_RESET) } returns tokenDom
            every { userPersistence.findById(user.id) } returns user
            every { passwordEncoder.encode("new-password") } returns "hashed-new-password"

            // when
            passwordResetService.resetPassword("valid-token", "new-password")

            // then
            verify {
                userPersistence.save(withArg {
                    assertThat(it.password).isEqualTo("hashed-new-password")
                })
            }
            verify { tokenPersistence.deleteByUserAndType(user, TokenTypeEnum.PASSWORD_RESET) }
        }

        @Test
        fun `resetPassword should throw InvalidTokenException when token not found`() {
            // given
            every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.PASSWORD_RESET) } returns null

            // when / then
            assertThatThrownBy { passwordResetService.resetPassword("invalid", "pwd") }
                .isInstanceOf(InvalidTokenException::class.java)
        }

        @Test
        fun `resetPassword should throw TokenExpiredException when token is expired`() {
            // given
            val expiredToken = PasswordResetToken(1, "expired-token", Instant.now().minusSeconds(10))
            every { tokenPersistence.findByTokenAndType("expired-token", TokenTypeEnum.PASSWORD_RESET) } returns expiredToken

            // when / then
            assertThatThrownBy { passwordResetService.resetPassword("expired-token", "pwd") }
                .isInstanceOf(TokenExpiredException::class.java)

            verify { tokenPersistence.deleteTokenAndType("expired-token", TokenTypeEnum.PASSWORD_RESET) }
        }

        @Test
        fun `resetPassword should throw UserNotFoundException when user missing`() {
            // given
            val tokenDom = PasswordResetToken(5, "valid-token", Instant.now().plusSeconds(100))
            every { tokenPersistence.findByTokenAndType("valid-token", TokenTypeEnum.PASSWORD_RESET) } returns tokenDom
            every { userPersistence.findById(5) } returns null

            // when / then
            assertThatThrownBy { passwordResetService.resetPassword("valid-token", "pwd") }
                .isInstanceOf(UserNotFoundException::class.java)
        }
    }

    @Nested
    inner class ValidateToken {
        @Test
        fun `validateToken should return true for valid token`() {
            // given
            val okToken = PasswordResetToken(2, "ok", Instant.now().plusSeconds(100))
            every { tokenPersistence.findByTokenAndType("ok", TokenTypeEnum.PASSWORD_RESET) } returns okToken

            // when
            val result = passwordResetService.validateToken("ok")

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `validateToken should return false for missing token`() {
            // given
            every { tokenPersistence.findByTokenAndType(any(), TokenTypeEnum.PASSWORD_RESET) } returns null

            // when
            val result = passwordResetService.validateToken("missing")

            // then
            assertThat(result).isFalse()
        }

        @Test
        fun `validateToken should return false and delete token when expired`() {
            // given
            val expiredToken = PasswordResetToken(1, "expired", Instant.now().minusSeconds(10))
            every { tokenPersistence.findByTokenAndType("expired", TokenTypeEnum.PASSWORD_RESET) } returns expiredToken

            // when
            val result = passwordResetService.validateToken("expired")

            // then
            assertThat(result).isFalse()
            verify { tokenPersistence.deleteTokenAndType("expired", TokenTypeEnum.PASSWORD_RESET) }
        }
    }
}
