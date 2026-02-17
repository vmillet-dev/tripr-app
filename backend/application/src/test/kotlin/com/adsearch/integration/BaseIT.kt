package com.adsearch.integration

import com.adsearch.integration.util.HttpUtil
import com.adsearch.integration.util.MailpitUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.client.RestTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
abstract class BaseIT {

    @LocalServerPort
    protected var port: Int = 0

    protected val restTemplate: RestTestClient by lazy {
        RestTestClient.bindToServer()
            .baseUrl("http://localhost:$port")
            .build()
    }

    @Autowired
    protected lateinit var mailpitUtil: MailpitUtil

    @Autowired
    protected lateinit var httpUtil: HttpUtil

    companion object {
        // Static containers that will be shared between all test classes
        private val POSTGRES_CONTAINER: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17.8")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .apply { start() }

        val MAILPIT_CONTAINER: GenericContainer<*> = GenericContainer("axllent/mailpit:v1.23")
            .withExposedPorts(1025, 8025)
            .apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { POSTGRES_CONTAINER.jdbcUrl }
            registry.add("spring.datasource.username") { POSTGRES_CONTAINER.username }
            registry.add("spring.datasource.password") { POSTGRES_CONTAINER.password }

            registry.add("spring.mail.host") { "localhost" }
            registry.add("spring.mail.port") { MAILPIT_CONTAINER.getMappedPort(1025) }
        }
    }
}
