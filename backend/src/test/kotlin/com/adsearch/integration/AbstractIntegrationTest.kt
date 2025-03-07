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
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
abstract class AbstractIntegrationTest {
    
    @LocalServerPort
    protected var port: Int = 0
    
    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withCommand("postgres", "-c", "max_connections=50", "-c", "log_statement=all")
            // Use container reuse to improve test performance
            withReuse(true)
            withStartupTimeout(java.time.Duration.ofSeconds(60))
        }
        
        @Container
        val mailpitContainer = GenericContainer<Nothing>("axllent/mailpit:v1.23").apply {
            withExposedPorts(1025, 8025)
            // Use container reuse to improve test performance
            withReuse(true)
            withStartupTimeout(java.time.Duration.ofSeconds(30))
        }
        
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            
            // Use Hibernate for schema management in tests
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
            registry.add("spring.liquibase.enabled") { "false" }
            registry.add("spring.sql.init.mode") { "never" }
            
            // Configure HikariCP for tests with more conservative settings
            registry.add("spring.datasource.hikari.connection-timeout") { "30000" }
            registry.add("spring.datasource.hikari.maximum-pool-size") { "5" }
            registry.add("spring.datasource.hikari.minimum-idle") { "2" }
            registry.add("spring.datasource.hikari.idle-timeout") { "30000" }
            registry.add("spring.datasource.hikari.max-lifetime") { "60000" }
            registry.add("spring.datasource.hikari.validation-timeout") { "5000" }
            registry.add("spring.datasource.hikari.auto-commit") { "true" }
            registry.add("spring.datasource.hikari.connection-test-query") { "SELECT 1" }
            registry.add("spring.datasource.hikari.leak-detection-threshold") { "30000" }
            
            registry.add("spring.mail.host") { "localhost" }
            registry.add("spring.mail.port") { mailpitContainer.getMappedPort(1025) }
            registry.add("spring.mail.username") { "" }
            registry.add("spring.mail.password") { "" }
            registry.add("spring.mail.properties.mail.smtp.auth") { "false" }
            registry.add("spring.mail.properties.mail.smtp.starttls.enable") { "false" }
            
            registry.add("mailpit.url") { "http://localhost:${mailpitContainer.getMappedPort(8025)}" }
        }
    }
}
