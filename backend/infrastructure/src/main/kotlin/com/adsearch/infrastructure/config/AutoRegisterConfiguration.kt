package com.adsearch.infrastructure.config

import com.adsearch.application.annotation.AutoRegister
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.context.annotation.Configuration
import org.springframework.core.type.filter.AnnotationTypeFilter

@Configuration
class AutoRegisterConfiguration : BeanDefinitionRegistryPostProcessor {

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        ClassPathBeanDefinitionScanner(registry, false).apply {
            addIncludeFilter(AnnotationTypeFilter(AutoRegister::class.java))
        }.scan("**.application.impl")
    }
}
