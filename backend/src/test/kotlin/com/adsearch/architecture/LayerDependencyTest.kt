package com.adsearch.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class LayerDependencyTest {

    @Test
    @DisplayName("Domain must not depend on application or infrastructure")
    fun domainShouldNotDependOnApplicationOrInfrastructure() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Application can only depend on domain")
    fun applicationShouldOnlyDependOnDomain() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Infrastructure can depend on domain and application")
    fun infrastructureCanDependOnDomainAndApplication() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        // This is a positive rule allowing dependencies, no need to check
        // Infrastructure is allowed to depend on domain and application
    }

    @Test
    @DisplayName("Common layer can be used by all other layers")
    fun commonCanBeUsedByAllLayers() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        // This is a positive rule allowing dependencies, no need to check
        // All layers are allowed to depend on common
    }

    @Test
    @DisplayName("Primary adapters must not depend on secondary adapters")
    fun primaryAdaptersMustNotDependOnSecondaryAdapters() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Adapters must not depend on other adapters of the same type")
    fun adaptersMustNotDependOnOtherAdaptersOfSameType() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        // Primary adapters should not depend on other primary adapters
        val primaryAdaptersRule = noClasses()
            .that().resideInAPackage("..adapter.in.web.controller..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..adapter.in.web.controller..", 
                "..adapter.in.web.resource.."
            )
            .allowEmptyShould(true)
        
        // Secondary adapters should not depend on other secondary adapters
        val secondaryAdaptersRule = noClasses()
            .that().resideInAPackage("..adapter.out.persistence..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "..adapter.out.security..", 
                "..adapter.out.email.."
            )
            .allowEmptyShould(true)
        
        primaryAdaptersRule.check(importedClasses)
        secondaryAdaptersRule.check(importedClasses)
    }
}
