package com.adsearch.integration.config

import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.ServerSetupTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
@Profile("test")
class MailConfig {
    companion object {
        var smtpPort: Int = ServerSetupTest.SMTP.port
    }

    @Bean
    fun greenMailBean(): GreenMailBean {
        return GreenMailBean()
    }

    @Bean
    fun mailSender(): JavaMailSenderImpl {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "localhost"
        mailSender.port = smtpPort

        return mailSender
    }
}
