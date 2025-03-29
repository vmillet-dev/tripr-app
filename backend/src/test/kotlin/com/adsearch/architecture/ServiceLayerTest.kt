package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Tests to verify service layer patterns
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ServiceLayerTest : BaseArchitectureTest() {
    
    @ArchTest
    val serviceClassesShouldBeAnnotatedWithServiceAnnotation: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Service")
        .and().resideInAPackage("$APPLICATION_SERVICE_PACKAGE..")
        .should().beAnnotatedWith(Service::class.java)
        .because("Service classes should be annotated with @Service")
    
    @ArchTest
    val servicesShouldNotAccessWebLayer: ArchRule = noClasses()
        .that().areAnnotatedWith(Service::class.java)
        .should().dependOnClassesThat().resideInAPackage("$INFRASTRUCTURE_WEB_PACKAGE..")
        .because("Service classes should not depend on web layer components")
    
    @ArchTest
    val serviceClassesShouldHaveServiceSuffix: ArchRule = classes()
        .that().areAnnotatedWith(Service::class.java)
        .should().haveSimpleNameEndingWith("Service")
        .because("Service classes should have 'Service' suffix")
    
    @ArchTest
    val transactionalClassesShouldBeServices: ArchRule = classes()
        .that().areAnnotatedWith(Transactional::class.java)
        .should().beAnnotatedWith(Service::class.java)
        .orShould().haveSimpleNameEndingWith("Service")
        .because("@Transactional should only be used in service classes")
}
