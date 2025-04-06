package com.adsearch.domain.config

import com.adsearch.domain.service.TokenService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfig {

    @Bean
    fun tokenService(): TokenService {
        return TokenService()
    }
}
