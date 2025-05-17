package com.adsearch.integration.config

import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.ServerSetup
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
@Profile("test")
class TestConfig {
    companion object {
        // Store the SMTP port for use in the DynamicPropertySource
        var smtpPort: Int = 0
            private set
            
        init {
            // Generate a random port offset between 3000-9000 to avoid conflicts in parallel tests
            val randomPortOffset = (3000..9000).random()
            
            // Set the SMTP port for the DynamicPropertySource method
            smtpPort = randomPortOffset + ServerSetup.SMTP.port
        }
    }
    
    @Bean
    fun greenMailBean(): GreenMailBean {
        // Create GreenMailBean with the same port offset used to calculate smtpPort
        return GreenMailBean().apply {
            portOffset = smtpPort - ServerSetup.SMTP.port
            
            // Enable SMTP protocol only
            setSmtpProtocol(true)
            setPop3Protocol(false)
            setImapProtocol(false)
            
            // Start automatically
            setAutostart(true)
        }
    }
    
    @Bean
    fun mailSender(): JavaMailSenderImpl {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = "localhost"
        mailSender.port = smtpPort
        
        return mailSender
    }
}
