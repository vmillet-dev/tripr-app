package com.adsearch.architecture

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.springframework.context.annotation.Configuration

/**
 * Tests to verify dependency management rules
 */
class DependencyManagementTest : BaseArchitectureTest() {
    
    @ArchTest
    val configurationClassesShouldBeInConfigPackage: ArchRule = classes()
        .that().areAnnotatedWith(Configuration::class.java)
        .should().resideInAnyPackage("$APPLICATION_CONFIG_PACKAGE..", "$INFRASTRUCTURE_CONFIG_PACKAGE..")
        .because("Configuration classes should be in a config package")
    
    @ArchTest
    val configurationClassesShouldHaveConfigSuffix: ArchRule = classes()
        .that().areAnnotatedWith(Configuration::class.java)
        .should().haveSimpleNameEndingWith("Config")
        .because("Configuration classes should have 'Config' suffix")
    
    @ArchTest
    val noSpringDependenciesInDomain: ArchRule = noClasses()
        .that().resideInAPackage("$DOMAIN_PACKAGE..")
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "jakarta.persistence..",
            "java.sql.."
        )
        .because("Domain classes should not directly depend on third-party libraries")
}
