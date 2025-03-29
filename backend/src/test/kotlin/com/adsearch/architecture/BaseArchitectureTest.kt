package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses


/**
 * Base class for architecture tests with common constants and configurations
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
abstract class BaseArchitectureTest {
    companion object {
        // Package constants
        const val BASE_PACKAGE = "com.adsearch"
        const val DOMAIN_PACKAGE = "$BASE_PACKAGE.domain"
        const val APPLICATION_PACKAGE = "$BASE_PACKAGE.application"
        const val INFRASTRUCTURE_PACKAGE = "$BASE_PACKAGE.infrastructure"
        
        // Subpackage constants
        const val DOMAIN_MODEL_PACKAGE = "$DOMAIN_PACKAGE.model"
        const val DOMAIN_PORT_PACKAGE = "$DOMAIN_PACKAGE.port"
        const val DOMAIN_EXCEPTION_PACKAGE = "$DOMAIN_PACKAGE.exception"
        
        const val APPLICATION_SERVICE_PACKAGE = "$APPLICATION_PACKAGE.service"
        const val APPLICATION_PORT_PACKAGE = "$APPLICATION_PACKAGE.port"
        const val APPLICATION_CONFIG_PACKAGE = "$APPLICATION_PACKAGE.config"
        
        const val INFRASTRUCTURE_ADAPTER_PACKAGE = "$INFRASTRUCTURE_PACKAGE.adapter"
        const val INFRASTRUCTURE_REPOSITORY_PACKAGE = "$INFRASTRUCTURE_PACKAGE.repository"
        const val INFRASTRUCTURE_WEB_PACKAGE = "$INFRASTRUCTURE_PACKAGE.web"
        const val INFRASTRUCTURE_CONFIG_PACKAGE = "$INFRASTRUCTURE_PACKAGE.config"
    }
}
