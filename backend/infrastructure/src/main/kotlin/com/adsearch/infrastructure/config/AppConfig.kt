package com.adsearch.infrastructure.config

import com.adsearch.application.impl.AuthenticationUseCaseImpl
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAsync
@EnableJpaRepositories(basePackages = ["**.infrastructure.adapter.out.persistence.jpa"])
@EntityScan(basePackages = ["**.infrastructure.adapter.out.persistence.entity"])
class AppConfig : BeanDefinitionRegistryPostProcessor {

    override fun postProcessBeanDefinitionRegistry(registry: BeanDefinitionRegistry) {
        val beansToRegister: MutableMap<String, Class<*>> = mutableMapOf(
            "authenticationUseCase" to AuthenticationUseCaseImpl::class.java
        )

        beansToRegister.forEach { (beanName: String, beanClass: Class<*>) ->
            val builder: BeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass)
            registry.registerBeanDefinition(beanName, builder.beanDefinition)
        }
    }
}
