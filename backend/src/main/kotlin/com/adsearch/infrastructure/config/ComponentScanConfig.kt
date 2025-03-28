package com.adsearch.infrastructure.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Configuration class to ensure all components are properly registered
 * after hexagonal architecture restructuring
 */
@Configuration
@ComponentScan(
    basePackages = [
        "com.adsearch.domain.port.repository",
        "com.adsearch.domain.port.service",
        "com.adsearch.infrastructure.adapter.persistence",
        "com.adsearch.infrastructure.adapter.service"
    ]
)
class ComponentScanConfig
