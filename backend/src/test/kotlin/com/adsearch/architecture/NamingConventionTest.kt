package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

/**
 * Tests to verify naming conventions for architectural components
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class NamingConventionTest : BaseArchitectureTest() {
    
    @ArchTest
    val domainPortsShouldHavePortSuffix: ArchRule = classes()
        .that().resideInAPackage("$DOMAIN_PORT_PACKAGE..")
        .should().haveSimpleNameEndingWith("Port")
        .because("Domain ports should have 'Port' suffix")
    
    @ArchTest
    val applicationServicesShouldHaveServiceSuffix: ArchRule = classes()
        .that().resideInAPackage("$APPLICATION_SERVICE_PACKAGE..")
        .should().haveSimpleNameEndingWith("Service")
        .because("Application services should have 'Service' suffix")
    
    @ArchTest
    val applicationPortsShouldHaveUseCaseSuffix: ArchRule = classes()
        .that().resideInAPackage("$APPLICATION_PORT_PACKAGE..")
        .should().haveSimpleNameEndingWith("UseCase")
        .because("Application ports should have 'UseCase' suffix")
    
    @ArchTest
    val infrastructureAdaptersShouldHaveAdapterSuffix: ArchRule = classes()
        .that().resideInAPackage("$INFRASTRUCTURE_ADAPTER_PACKAGE..")
        .and().doNotHaveSimpleName("AdSearchAdapter")
        .should().haveSimpleNameEndingWith("Adapter")
        .because("Infrastructure adapters should have 'Adapter' suffix")
    
    @ArchTest
    val infrastructureRepositoriesShouldHaveRepositorySuffix: ArchRule = classes()
        .that().resideInAPackage("$INFRASTRUCTURE_REPOSITORY_PACKAGE..")
        .and().doNotHaveSimpleName("BaseRepository")
        .and().resideOutsideOfPackage("$INFRASTRUCTURE_REPOSITORY_PACKAGE.jpa..")
        .and().resideOutsideOfPackage("$INFRASTRUCTURE_REPOSITORY_PACKAGE.entity..")
        .should().haveSimpleNameEndingWith("Repository")
        .because("Infrastructure repositories should have 'Repository' suffix")
    
    @ArchTest
    val infrastructureControllersShouldHaveControllerSuffix: ArchRule = classes()
        .that().resideInAPackage("$INFRASTRUCTURE_WEB_PACKAGE.controller..")
        .should().haveSimpleNameEndingWith("Controller")
        .because("Infrastructure controllers should have 'Controller' suffix")
}
