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
        val postgresContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:17.3")).apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }
        
        @Container
        val mailpitContainer = GenericContainer<Nothing>(DockerImageName.parse("axllent/mailpit:v1.23")).apply {
            withExposedPorts(1025, 8025)
        }
        
        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.jdbcUrl }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            
            registry.add("spring.mail.host") { "localhost" }
            registry.add("spring.mail.port") { mailpitContainer.getMappedPort(1025) }
        }
    }
}
