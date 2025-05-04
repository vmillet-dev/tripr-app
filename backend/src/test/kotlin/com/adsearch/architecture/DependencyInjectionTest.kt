package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

class DependencyInjectionTest : BaseArchitecture() {
    @Test
    @DisplayName("No use of @Autowired on fields")
    fun noUseOfAutowiredOnFields() {
        // No fields should be annotated with @Autowired
        val noSpringFieldInjection = noFields()
            .should().beAnnotatedWith(Autowired::class.java)

        // No methods should be annotated with @Autowired
        val noSpringMethodInjection = noMethods()
            .should().beAnnotatedWith(Autowired::class.java)

        // Check all rules
        noSpringFieldInjection.check(importedClasses)
        noSpringMethodInjection.check(importedClasses)
    }

    @Test
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
