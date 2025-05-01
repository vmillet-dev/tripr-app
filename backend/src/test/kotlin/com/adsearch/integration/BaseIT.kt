package com.adsearch.integration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
abstract class BaseIT {

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    companion object {
        // Static containers that will be shared between all test classes
        private val POSTGRES_CONTAINER: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:17.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .apply { start() }

        private val MAILPIT_CONTAINER: GenericContainer<*> = GenericContainer("axllent/mailpit:v1.23")
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

    protected fun buildJsonPayload(payload: Any, token: String? = null): HttpEntity<*> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        if (token != null) {
            headers.add("Cookie", "refresh-token=${token}; Max-Age=604800000; Expires=Thu")
        }

        return HttpEntity(payload, headers)
    }
}
