package com.adsearch.infrastructure.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAsync
@EnableJpaRepositories(basePackages = ["**.infrastructure.adapter.out.persistence.jpa"])
@EntityScan(basePackages = ["**.infrastructure.adapter.out.persistence.entity"])
class AppConfig
