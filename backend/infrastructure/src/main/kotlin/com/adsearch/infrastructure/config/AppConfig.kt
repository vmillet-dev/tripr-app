package com.adsearch.infrastructure.config

import com.adsearch.application.annotation.AutoRegister
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAsync
@EnableJpaRepositories(basePackages = ["**.infrastructure.adapter.out.persistence.jpa"])
@EntityScan(basePackages = ["**.infrastructure.adapter.out.persistence.entity"])
class AppConfig : BeanDefinitionRegistryPostProcessor {

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        ClassPathBeanDefinitionScanner(registry, false).apply {
            addIncludeFilter(AnnotationTypeFilter(AutoRegister::class.java))
        }.scan("**.application.impl")
    }
}
