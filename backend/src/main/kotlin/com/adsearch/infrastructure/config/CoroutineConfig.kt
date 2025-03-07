package com.adsearch.infrastructure.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for coroutine dispatchers
 */
@Configuration
class CoroutineConfig {
    
    @Bean
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
