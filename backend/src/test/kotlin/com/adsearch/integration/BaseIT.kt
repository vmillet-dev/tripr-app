package com.adsearch.integration

import com.adsearch.integration.config.TestConfig
import com.adsearch.integration.util.GreenMailUtil
import com.adsearch.integration.util.HttpUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
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
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:h2:mem:testdb_${UUID.randomUUID()};DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;MODE=PostgreSQL" }
            registry.add("spring.mail.host") { "localhost" }
            registry.add("spring.mail.port") { TestConfig.smtpPort }
        }
    }
}
