package com.adsearch.architecture

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController

/**
 * Tests to verify specific hexagonal architecture principles
 */
class HexagonalArchitectureTest : BaseArchitectureTest() {
    
    @ArchTest
    val domainShouldNotUseSpringAnnotations: ArchRule = noClasses()
        .that().resideInAPackage("$DOMAIN_PACKAGE..")
        .should().beAnnotatedWith(Service::class.java)
        .orShould().beAnnotatedWith(Repository::class.java)
        .orShould().beAnnotatedWith(RestController::class.java)
        .because("Domain layer should not use Spring annotations")
    
    @ArchTest
    val domainShouldNotAccessDatabase: ArchRule = noClasses()
        .that().resideInAPackage("$DOMAIN_PACKAGE..")
        .should().accessClassesThat().areAssignableTo(JpaRepository::class.java)
        .because("Domain layer should not access database directly")
    
    @ArchTest
    val domainModelShouldNotHavePersistenceAnnotations: ArchRule = noClasses()
        .that().resideInAPackage("$DOMAIN_MODEL_PACKAGE..")
        .should().beAnnotatedWith("jakarta.persistence.Entity")
        .orShould().beAnnotatedWith("jakarta.persistence.Table")
        .because("Domain models should not have persistence annotations")
    
    @ArchTest
    val infrastructureShouldAccessDomainOnlyThroughPorts: ArchRule = noClasses()
        .that().resideInAPackage("$INFRASTRUCTURE_PACKAGE..")
        .and().resideOutsideOfPackage("$INFRASTRUCTURE_REPOSITORY_PACKAGE..")
        .should().accessClassesThat().resideInAPackage("$DOMAIN_MODEL_PACKAGE..")
        .because("Infrastructure should access domain models but not implement domain ports directly")
    
    @ArchTest
    val applicationServicesShouldOrchestrate: ArchRule = classes()
        .that().resideInAPackage("$APPLICATION_SERVICE_PACKAGE..")
        .should().accessClassesThat().resideInAPackage("$DOMAIN_PORT_PACKAGE..")
        .because("Application services should orchestrate between domain and infrastructure through ports")
}
