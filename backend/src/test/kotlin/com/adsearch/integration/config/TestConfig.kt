package com.adsearch.integration.config

import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.ServerSetupTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
@Profile("test")
class TestConfig {
    companion object {
        // Store the SMTP port for use in the DynamicPropertySource
        // ServerSetupTest.SMTP is the official approach for test port allocation
        var smtpPort: Int = ServerSetupTest.SMTP.port
            private set
    }
    
    @Bean
    fun greenMailBean(): GreenMailBean {
        // Create GreenMailBean with default settings
        // It will use ServerSetupTest.SMTP (port 3025) by default
        return GreenMailBean()
        // The bean is autoconfigured to start automatically
    }
    
    @Bean
    fun mailSender(): JavaMailSenderImpl {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "localhost"
        mailSender.port = smtpPort
        
        return mailSender
    }
}
