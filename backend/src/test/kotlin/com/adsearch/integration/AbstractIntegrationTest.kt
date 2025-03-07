package com.adsearch.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
abstract class AbstractIntegrationTest {
    
    @LocalServerPort
    protected var port: Int = 0
    
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
            
            // Configure JPA to create-drop for tests
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.liquibase.enabled") { "false" }
            
            // Configure HikariCP for tests with more conservative settings
            registry.add("spring.datasource.hikari.maximum-pool-size") { "3" }
            registry.add("spring.datasource.hikari.minimum-idle") { "1" }
            registry.add("spring.datasource.hikari.idle-timeout") { "10000" }
            registry.add("spring.datasource.hikari.max-lifetime") { "20000" }
            registry.add("spring.datasource.hikari.connection-timeout") { "10000" }
            registry.add("spring.datasource.hikari.validation-timeout") { "2000" }
            registry.add("spring.datasource.hikari.leak-detection-threshold") { "30000" }
            registry.add("spring.datasource.hikari.connection-test-query") { "SELECT 1" }
            
            registry.add("spring.mail.host") { "localhost" }
            registry.add("spring.mail.port") { MAILPIT_CONTAINER.getMappedPort(1025) }
        }
    }
}
