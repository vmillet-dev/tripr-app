package com.adsearch.infrastructure.adapter.out.notification

import com.adsearch.domain.exception.MailSendException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.mail.internet.MimeMessage
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

class EmailServiceAdapterTest {

    private val mailSender = mockk<JavaMailSender>()
    private val templateEngine = mockk<TemplateEngine>()
    private val baseUrl = "http://localhost:8080"
    private val from = "noreply@example.com"
    private val adapter = EmailServiceAdapter(mailSender, templateEngine, baseUrl, from)

    @Test
    fun `sendPasswordResetEmail should send email when successful`() {
        // given
        val mimeMessage = mockk<MimeMessage>(relaxed = true)

        every { mailSender.createMimeMessage() } returns mimeMessage
        every { templateEngine.process(any<String>(), any<Context>()) } returns "<html>Body</html>"
        every { mailSender.send(any<MimeMessage>()) } returns Unit

        // when
        adapter.sendPasswordResetEmail("user@e.com", "reset-token")

        // then
        verify { mailSender.createMimeMessage() }
        verify { templateEngine.process("email/password-reset-email", any<Context>()) }
        verify { mailSender.send(mimeMessage) }
    }

    @Test
    fun `sendPasswordResetEmail should throw MailSendException when sending fails`() {
        // given
        every { mailSender.createMimeMessage() } throws RuntimeException("Connection failed")

        // when / then
        assertThatThrownBy { adapter.sendPasswordResetEmail("user@e.com", "reset-token") }
            .isInstanceOf(MailSendException::class.java)
            .hasMessageContaining("user@e.com")
    }
}
