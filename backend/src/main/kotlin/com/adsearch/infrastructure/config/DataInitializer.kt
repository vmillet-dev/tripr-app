package com.adsearch.infrastructure.config

import com.adsearch.domain.model.Role
import com.adsearch.domain.model.RoleType
import com.adsearch.domain.port.RolePersistencePort
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitializer {
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    @Bean
    fun initRoles(rolePersistencePort: RolePersistencePort): CommandLineRunner = CommandLineRunner {
        logger.info("Initializing roles...")
        
        // Create USER role if it doesn't exist
        if (rolePersistencePort.findByName(RoleType.USER) == null) {
            logger.info("Creating USER role")
            rolePersistencePort.save(Role(name = RoleType.USER))
        }
        
        // Create ADMIN role if it doesn't exist
        if (rolePersistencePort.findByName(RoleType.ADMIN) == null) {
            logger.info("Creating ADMIN role")
            rolePersistencePort.save(Role(name = RoleType.ADMIN))
        }
    }
}
