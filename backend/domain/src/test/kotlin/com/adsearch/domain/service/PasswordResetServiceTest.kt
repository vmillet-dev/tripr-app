package com.adsearch.domain.service

import com.adsearch.domain.exception.InvalidTokenException
import com.adsearch.domain.exception.TokenExpiredException
import com.adsearch.domain.exception.UserNotFoundException
import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.model.enums.TokenTypeEnum
import com.adsearch.domain.model.enums.UserRoleEnum
import com.adsearch.domain.port.out.ConfigurationProviderPort
import com.adsearch.domain.port.out.notification.EmailServicePort
import com.adsearch.domain.port.out.persistence.TokenPersistencePort
import com.adsearch.domain.port.out.persistence.UserPersistencePort
import com.adsearch.domain.port.out.security.PasswordEncoderPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.Instant

class PasswordResetServiceTest {

    private val configurationProvider = mockk<ConfigurationProviderPort>(relaxed = true)
    private val emailService = mockk<EmailServicePort>(relaxed = true)
    private val passwordEncoder = mockk<PasswordEncoderPort>(relaxed = true)
    private val userPersistence = mockk<UserPersistencePort>(relaxed = true)
    private val tokenPersistence = mockk<TokenPersistencePort>(relaxed = true)

    private val service = PasswordResetService(
        configurationProvider,
        emailService,
        passwordEncoder,
        userPersistence,
        tokenPersistence
    )

    @Test
    fun `requestPasswordReset should save token and send email when user exists`() {
        val user = UserDom(7, "bob", "bob@e.com", "p", setOf(UserRoleEnum.ROLE_USER.type), true)
        every { userPersistence.findByUsername("bob") } returns user
        every { configurationProvider.getPasswordResetTokenExpiration() } returns 3600L

        service.requestPasswordReset("bob")

        verify { tokenPersistence.deleteByUserId(user.id, TokenTypeEnum.PASSWORD_RESET) }
        verify { tokenPersistence.save(ofType(PasswordResetTokenDom::class)) }
        verify { emailService.sendPasswordResetEmail(user.email, any()) }
    }

    @Test
    fun `requestPasswordReset should throw UserNotFoundException when user missing`() {
        every { userPersistence.findByUsername("no") } returns null
        assertThatThrownBy { service.requestPasswordReset("no") }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `resetPassword should update password when token valid`() {
        val user = UserDom(8, "c", "c@c.com", "old", setOf(UserRoleEnum.ROLE_USER.type), true)
        val tokenDom = PasswordResetTokenDom(user.id, "tok", Instant.now().plusSeconds(1000))
        every { tokenPersistence.findByToken("tok", TokenTypeEnum.PASSWORD_RESET) } returns tokenDom
        every { userPersistence.findById(user.id) } returns user
        every { passwordEncoder.encode("new") } returns "hnew"

        service.resetPassword("tok", "new")

        verify { userPersistence.save(user.changePassword("hnew")) }
        verify { tokenPersistence.deleteByUserId(user.id, TokenTypeEnum.PASSWORD_RESET) }
    }

    @Test
    fun `resetPassword should throw when token invalid or expired or user missing`() {
        every { tokenPersistence.findByToken("no", TokenTypeEnum.PASSWORD_RESET) } returns null
        assertThatThrownBy { service.resetPassword("no", "x") }
            .isInstanceOf(InvalidTokenException::class.java)

        val expired = PasswordResetTokenDom(1, "e", Instant.now().minusSeconds(10))
        every { tokenPersistence.findByToken("e", TokenTypeEnum.PASSWORD_RESET) } returns expired
        assertThatThrownBy { service.resetPassword("e", "x") }
            .isInstanceOf(TokenExpiredException::class.java)
        verify { tokenPersistence.deleteByToken("e", TokenTypeEnum.PASSWORD_RESET) }

        val t = PasswordResetTokenDom(5, "t", Instant.now().plusSeconds(100))
        every { tokenPersistence.findByToken("t", TokenTypeEnum.PASSWORD_RESET) } returns t
        every { userPersistence.findById(5) } returns null
        assertThatThrownBy { service.resetPassword("t", "x") }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `validateToken should return false for missing or expired token and true otherwise`() {
        every { tokenPersistence.findByToken("no", TokenTypeEnum.PASSWORD_RESET) } returns null
        assertThat(service.validateToken("no")).isFalse()

        val expired = PasswordResetTokenDom(1, "e", Instant.now().minusSeconds(10))
        every { tokenPersistence.findByToken("e", TokenTypeEnum.PASSWORD_RESET) } returns expired
        assertThat(service.validateToken("e")).isFalse()
        verify { tokenPersistence.deleteByToken("e", TokenTypeEnum.PASSWORD_RESET) }

        val ok = PasswordResetTokenDom(2, "ok", Instant.now().plusSeconds(100))
        every { tokenPersistence.findByToken("ok", TokenTypeEnum.PASSWORD_RESET) } returns ok
        assertThat(service.validateToken("ok")).isTrue()
    }
}
