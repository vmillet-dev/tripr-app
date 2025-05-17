package com.adsearch.integration

import com.adsearch.integration.util.GreenMailUtil
import com.adsearch.integration.util.HttpUtil
import com.icegreen.greenmail.spring.GreenMailBean
import com.icegreen.greenmail.util.ServerSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class BaseIT {

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var greenMailUtil: GreenMailUtil

    @Autowired
    protected lateinit var httpUtil: HttpUtil

    companion object {
        // Store the SMTP port for use in the DynamicPropertySource
        private var smtpPort: Int = 0
        
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:h2:mem:testdb_${UUID.randomUUID()};DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;MODE=PostgreSQL" }
            registry.add("spring.mail.host") { "localhost" }
            registry.add("spring.mail.port") { smtpPort }
        }
    }
    
    @Configuration
    @Profile("test")
    class TestConfig {
        init {
            // Generate a random port offset between 3000-9000 to avoid conflicts in parallel tests
            val randomPortOffset = (3000..9000).random()
            
            // Set the SMTP port for the DynamicPropertySource method
            smtpPort = randomPortOffset + ServerSetup.SMTP.port
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
}
