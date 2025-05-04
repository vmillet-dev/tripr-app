package com.adsearch.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class LayerDependencyTest : BaseArchitecture() {

    @Test
    @DisplayName("Domain must not depend on application or infrastructure")
    fun domainShouldNotDependOnApplicationOrInfrastructure() {
        val rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Application can only depend on domain")
    fun applicationShouldOnlyDependOnDomain() {
        val rule = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Primary adapters must not depend on secondary adapters")
    fun primaryAdaptersMustNotDependOnSecondaryAdapters() {
        val rule = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..")

        rule.check(importedClasses)
    }
}
