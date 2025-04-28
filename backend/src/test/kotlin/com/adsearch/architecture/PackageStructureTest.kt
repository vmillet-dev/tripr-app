package com.adsearch.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class PackageStructureTest {

    @Test
    @DisplayName("Packages must follow a consistent structure")
    fun packagesMustFollowConsistentStructure() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        // This is mostly a documentation test; the specific structure is enforced by other tests
        // We can check that the main packages exist with classes in them
        val mainPackages = listOf("domain", "application", "infrastructure", "adapter")
        
        for (pkg in mainPackages) {
            val packagesExist = importedClasses.any { it.packageName.contains(".$pkg.") }
            assert(packagesExist) { "Package structure should include $pkg package" }
        }
    }

    @Test
    @DisplayName("Packages must not have cyclic dependencies")
    fun packagesMustNotHaveCyclicDependencies() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = SlicesRuleDefinition.slices()
            .matching("com.adsearch.(*)..")
            .should().beFreeOfCycles()
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Packages at the same level must not depend on each other")
    fun packagesAtSameLevelMustNotDependOnEachOther() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        // Check that packages at the same level do not depend on each other
        val rule = SlicesRuleDefinition.slices()
            .matching("com.adsearch.domain.(*)..")
            .should().notDependOnEachOther()
        
        rule.check(importedClasses)
    }
}
