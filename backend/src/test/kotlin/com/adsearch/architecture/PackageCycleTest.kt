package com.adsearch.architecture

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition

/**
 * Tests to verify that there are no package cycles in the codebase
 */
class PackageCycleTest : BaseArchitectureTest() {
    
    @ArchTest
    val noPackageCycles: ArchRule = SlicesRuleDefinition.slices()
        .matching("$BASE_PACKAGE.(*)..")
        .should().beFreeOfCycles()
        .because("Package cycles lead to tight coupling and make the codebase harder to maintain")
    
    @ArchTest
    val noSubPackageCycles: ArchRule = SlicesRuleDefinition.slices()
        .matching("$BASE_PACKAGE.(*).(*)..")
        .should().beFreeOfCycles()
        .because("Subpackage cycles lead to tight coupling and make the codebase harder to maintain")
}
