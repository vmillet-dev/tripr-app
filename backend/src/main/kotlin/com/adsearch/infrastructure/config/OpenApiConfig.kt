package com.adsearch.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * OpenAPI configuration for development profile
 * This configuration does not include security requirements, making Swagger UI accessible without authentication
 */
@Configuration
@Profile("dev")
class OpenApiConfig {

    @Bean
    fun devOpenAPI(): OpenAPI {
        return OpenAPI().info(
                Info()
                    .title("Example (Development)")
                    .version("1.0")
                    .description("An example API for auth workflow - Development Mode")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            ).components(Components())
    }
}
