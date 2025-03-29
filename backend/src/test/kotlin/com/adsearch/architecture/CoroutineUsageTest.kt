package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import kotlinx.coroutines.Dispatchers

/**
 * Tests to verify coroutine usage patterns
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class CoroutineUsageTest : BaseArchitectureTest() {
    
    @ArchTest
    val controllersShouldBeInCorrectPackage: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Controller")
        .should().resideInAPackage("$INFRASTRUCTURE_WEB_PACKAGE.controller..")
        .because("Controllers should be in the controller package")
    
    @ArchTest
    val noDirectUseOfDispatchersInDomain: ArchRule = noClasses()
        .that().resideInAPackage("$DOMAIN_PACKAGE..")
        .should().accessClassesThat().belongToAnyOf(Dispatchers::class.java)
        .because("Domain classes should not directly use Dispatchers")
}
