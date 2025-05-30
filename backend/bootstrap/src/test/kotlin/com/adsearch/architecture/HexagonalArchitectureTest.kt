package com.adsearch.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

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

    @Test
    @DisplayName("Use case interface must be implemented in impl package")
    fun primaryPortsMustBeImplementedByUseCases() {
        val interfaces = importedClasses.filter {
            it.packageName.contains(".application") && it.isInterface
        }

        for (interfaceClass in interfaces) {
            val implementers = importedClasses.filter {
                !it.isInterface && it.interfaces.contains(interfaceClass)
            }

            val hasImplementationInApplicationImpl = implementers.any {
                it.packageName.contains(".application.impl")
            }

            assert(hasImplementationInApplicationImpl) {
                "Interface ${interfaceClass.name} should be implemented by a class in application.impl package"
            }
        }
    }
}
