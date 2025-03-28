package com.adsearch.integration.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

/**
 * Test configuration to ensure all components are properly scanned
 * after hexagonal architecture restructuring
 */
@TestConfiguration
@ComponentScan(
    basePackages = [
        "com.adsearch.domain.port.repository",
        "com.adsearch.domain.port.service",
        "com.adsearch.infrastructure.adapter.persistence",
        "com.adsearch.infrastructure.adapter.service"
    ],
    includeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = [
                "com\\.adsearch\\.domain\\.port\\.repository\\..*Port",
                "com\\.adsearch\\.domain\\.port\\.service\\..*Port",
                "com\\.adsearch\\.infrastructure\\.adapter\\.persistence\\..*Adapter",
                "com\\.adsearch\\.infrastructure\\.adapter\\.service\\..*Adapter"
            ]
        )
    ]
)
class TestConfig
