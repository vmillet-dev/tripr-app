package com.adsearch.config

import com.adsearch.domain.annotation.AutoRegister
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
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
@EnableJpaRepositories(basePackages = ["com.adsearch.infrastructure.adapter.out.persistence.jpa"])
@EntityScan(basePackages = ["com.adsearch.infrastructure.adapter.out.persistence.entity"])
class AppConfig : BeanDefinitionRegistryPostProcessor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        ClassPathBeanDefinitionScanner(registry, false)
            .apply { addIncludeFilter(AnnotationTypeFilter(AutoRegister::class.java)) }
            .findCandidateComponents("com.adsearch.domain.service")
            .forEach { beanDef ->
                runCatching {
                    val clazz = Class.forName(beanDef.beanClassName)
                    val name = clazz.simpleName.replaceFirstChar { it.lowercase() }

                    registry.registerBeanDefinition(name, beanDef)
                    clazz.interfaces.forEach { iface ->
                        registry.registerAlias(name, iface.simpleName.replaceFirstChar { it.lowercase() })
                    }
                    log.info("✅ Registered: {}", name)
                }.onFailure { log.error("❌ Failed: {}", it.message) }
            }
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {}
}
