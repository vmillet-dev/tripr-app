package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

/**
 * Tests to verify package-level dependencies follow hexagonal architecture principles
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class PackageDependencyTest : BaseArchitectureTest() {
    
    @ArchTest
    val domainDoesNotDependOnOtherLayers: ArchRule = noClasses()
        .that().resideInAPackage("$DOMAIN_PACKAGE..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "$APPLICATION_PACKAGE..",
            "$INFRASTRUCTURE_PACKAGE.."
        )
        .because("Domain layer should not depend on application or infrastructure layers")
    
    @ArchTest
    val applicationDoesNotDependOnInfrastructure: ArchRule = noClasses()
        .that().resideInAPackage("$APPLICATION_PACKAGE..")
        .should().dependOnClassesThat().resideInAPackage("$INFRASTRUCTURE_PACKAGE..")
        .because("Application layer should not depend on infrastructure layer")
    
    @ArchTest
    val infrastructureImplementsInterfaces: ArchRule = classes()
        .that().resideInAPackage("$INFRASTRUCTURE_REPOSITORY_PACKAGE..")
        .and().resideOutsideOfPackage("$INFRASTRUCTURE_REPOSITORY_PACKAGE.jpa")
        .and().haveSimpleNameEndingWith("Repository")
        .should().implement(com.tngtech.archunit.base.DescribedPredicate.describe("domain port interfaces")
        { javaClass -> javaClass.packageName.startsWith(DOMAIN_PORT_PACKAGE) })
        .because("Infrastructure repositories should implement domain port interfaces")
    
    @ArchTest
    val adaptersShouldImplementPorts: ArchRule = classes()
        .that().resideInAPackage("$INFRASTRUCTURE_ADAPTER_PACKAGE..")
        .and().haveSimpleNameEndingWith("Adapter")
        .should().implement(com.tngtech.archunit.base.DescribedPredicate.describe("domain port interfaces")
        { javaClass -> javaClass.packageName.startsWith(DOMAIN_PORT_PACKAGE) })
        .orShould().beAssignableTo(com.tngtech.archunit.base.DescribedPredicate.describe("adapter classes")
        { javaClass -> javaClass.packageName.startsWith(INFRASTRUCTURE_ADAPTER_PACKAGE) })
        .because("Adapters should implement domain ports or extend other adapters")
}
