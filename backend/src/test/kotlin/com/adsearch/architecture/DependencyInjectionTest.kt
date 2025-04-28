package com.adsearch.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.stereotype.Repository

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class DependencyInjectionTest {

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Dependency injection must be done through constructors")
    fun dependencyInjectionMustBeDoneThroughConstructors() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = noMethods()
            .that().areAnnotatedWith(Autowired::class.java)
            .should().beDeclaredInClassesThat().resideInAPackage("..component..")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("No use of @Autowired on fields")
    fun noUseOfAutowiredOnFields() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = noFields()
            .that().areAnnotatedWith(Autowired::class.java)
            .should().beDeclaredInClassesThat().resideInAPackage("com.adsearch..")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Spring beans must be properly annotated")
    fun springBeansMustBeProperlyAnnotated() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        // Check use cases are annotated with @Service
        val useCaseRule = classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .or().haveSimpleNameEndingWith("Service")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Service::class.java)
        
        // Check adapters are annotated with @Component or @Repository
        val adapterRule = classes()
            .that().haveSimpleNameEndingWith("Adapter")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Component::class.java)
            .orShould().beAnnotatedWith(Repository::class.java)
        
        useCaseRule.check(importedClasses)
        adapterRule.check(importedClasses)
    }
}
