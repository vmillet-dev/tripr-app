package com.adsearch.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

class HexagonalArchitectureTest : BaseArchitecture() {

    @Test
    @DisplayName("Domain must not depend on application or infrastructure")
    fun domainShouldNotDependOnApplicationOrInfrastructure() {
        val rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..")

        rule.check(importedClasses)
    }

    @Test
    @Disabled
    @DisplayName("Application can only depend on domain")
    fun applicationShouldOnlyDependOnDomain() {
        val rule = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")

        rule.check(importedClasses)
    }

    @Test
    @Disabled
    @DisplayName("Primary adapters must not depend on secondary adapters")
    fun primaryAdaptersMustNotDependOnSecondaryAdapters() {
        val rule = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..")

        rule.check(importedClasses)
    }

    @Test
    @Disabled
    @DisplayName("@Service should only be used in infrastructure.service package")
    fun serviceShouldOnlyBeUsedInInfrastructureService() {
        val rule = classes()
            .that().areAnnotatedWith(Service::class.java)
            .should().resideInAPackage("..infrastructure.service..")
            .`as`("@Service should only be used in 'infrastructure.service' package")

        rule.check(importedClasses)
    }


    @Test
    @DisplayName("@Component should only be used in 'adapter' package by Adapter, Filter, or MapperImpl classes")
    fun componentShouldOnlyBeUsedInAdapterPackageOnExpectedClasses() {
        val rule = classes()
            .that().areAnnotatedWith(Component::class.java)
            .should().resideInAPackage("..adapter..")
            .andShould().haveSimpleNameEndingWith("Adapter")
            .orShould().haveSimpleNameEndingWith("Filter")
            .orShould().haveSimpleNameEndingWith("MapperImpl")
            .`as`("@Component should only be used in 'adapter' package on *Adapter, *Filter or *MapperImpl classes")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("application package should not depend on Spring or other frameworks")
    fun applicationShouldNotDependOnFrameworks() {
        val rule = noClasses()
            .that().resideInAPackage("..application..")
            .or().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "jakarta..",
                "javax..",
                "com.fasterxml..",
                "org.hibernate.."
            )
            .`as`("Application layer must not depend on frameworks (Spring, Jakarta, etc.)")

        rule.check(importedClasses)
    }
}
