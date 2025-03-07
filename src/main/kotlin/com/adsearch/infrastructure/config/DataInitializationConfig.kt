package com.adsearch.infrastructure.config

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserRepositoryPort
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

@Configuration
class DataInitializationConfig {
    
    private val logger = LoggerFactory.getLogger(DataInitializationConfig::class.java)
    
    @Bean
    fun dataInitializer(
        userRepository: UserRepositoryPort,
        passwordEncoder: PasswordEncoder
    ): CommandLineRunner {
        return CommandLineRunner {
            runBlocking {
                // Create a test user if it doesn't exist
                if (userRepository.findByUsername("user") == null) {
                    val user = User(
                        id = UUID.randomUUID(),
                        username = "user",
                        password = passwordEncoder.encode("password"),
                        roles = mutableListOf("USER")
                    )
                    userRepository.save(user)
                    logger.info("Created test user: ${user.username}")
                }
                
                // Create an admin user if it doesn't exist
                if (userRepository.findByUsername("admin") == null) {
                    val admin = User(
                        id = UUID.randomUUID(),
                        username = "admin",
                        password = passwordEncoder.encode("admin"),
                        roles = mutableListOf("USER", "ADMIN")
                    )
                    userRepository.save(admin)
                    logger.info("Created admin user: ${admin.username}")
                }
            }
        }
    }
}
