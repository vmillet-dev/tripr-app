package com.adsearch.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!dev")
class OpenApiConfig {
    
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Ad Search API")
                    .version("1.0")
                    .description("API for searching ads across multiple external sources")
                    .license(License().name("Apache 2.0").url("http://springdoc.org"))
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearer-jwt", 
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .`in`(SecurityScheme.In.HEADER)
                            .name("Authorization")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList("bearer-jwt"))
    }
}
