package com.adsearch.integration

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base class for all integration tests.
 * 
 * This class sets up the Spring Boot test environment with a random port,
 * configures TestContainers for PostgreSQL and Mailpit, and provides
 * common functionality for all integration tests.
 * 
 * The PostgreSQL container is shared across all tests to improve performance,
 * but each test should clean up its own data to ensure test isolation.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
abstract class AbstractIntegrationTest {
    
    @LocalServerPort
    protected var port: Int = 0
    
    @Autowired
    protected lateinit var jdbcTemplate: JdbcTemplate
    
    /**
     * Setup method that runs before each test.
     * Override this method in subclasses to set up test data.
     */
    @BeforeEach
    open fun setUp() {
        // Base setup - can be extended by subclasses
    }
    
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
    
    /**
     * Cleanup method that runs after each test.
     * This method helps with test isolation.
     * Override this method in subclasses to add custom cleanup logic.
     */
    @AfterEach
    open fun cleanupConnections() {
        // Subclasses can implement specific cleanup logic
    }
}
