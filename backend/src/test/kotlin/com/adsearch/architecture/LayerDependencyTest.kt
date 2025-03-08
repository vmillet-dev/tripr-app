package com.adsearch.architecture

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture

/**
 * Tests to verify that layer dependencies follow hexagonal architecture principles
 */
class LayerDependencyTest : BaseArchitectureTest() {
    
    @ArchTest
    val layerDependenciesAreRespected: ArchRule = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Domain").definedBy(DOMAIN_PACKAGE)
        .layer("Application").definedBy(APPLICATION_PACKAGE)
        .layer("Infrastructure").definedBy(INFRASTRUCTURE_PACKAGE)
        .whereLayer("Domain").mayNotAccessAnyLayer()
        .whereLayer("Application").mayOnlyAccessLayers("Domain")
        .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application")
}
