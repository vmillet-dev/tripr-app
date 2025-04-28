package com.adsearch.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class NamingConventionTest {

    @Test
    @DisplayName("Classes in enum packages must end with Enum")
    fun enumClassesShouldEndWithEnum() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..enum..")
            .should().haveSimpleNameEndingWith("Enum")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Classes in dto packages must end with Dto")
    fun dtoClassesShouldEndWithDto() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..dto..")
            .should().haveSimpleNameEndingWith("Dto")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Classes in domain/model must end with Dom")
    fun domainModelClassesShouldEndWithDom() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..domain.model..")
            .should().haveSimpleNameEndingWith("Dom")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Classes in domain/port must be interfaces ending with Port")
    fun domainPortClassesShouldBeInterfacesEndingWithPort() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..domain.port..")
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Port")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Primary adapters must end with Controller, Resource, or Adapter")
    fun primaryAdaptersShouldHaveCorrectSuffix() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..adapter.in..")
            .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("Resource")
            .orShould().haveSimpleNameEndingWith("Adapter")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Secondary adapters must end with Repository, Client, or Adapter")
    fun secondaryAdaptersShouldHaveCorrectSuffix() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..adapter.out..")
            .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameEndingWith("Client")
            .orShould().haveSimpleNameEndingWith("Adapter")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Use cases must end with UseCase or Service")
    fun useCasesShouldHaveCorrectSuffix() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..application..")
            .and().haveSimpleNameNotEndingWith("Impl")
            .should().haveSimpleNameEndingWith("UseCase")
            .orShould().haveSimpleNameEndingWith("Service")
        
        rule.check(importedClasses)
    }

    @Test
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("Mappers must end with Mapper")
    fun mappersShouldEndWithMapper() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..mapper..")
            .should().haveSimpleNameEndingWith("Mapper")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Configurations must end with Config or Configuration")
    fun configurationsShouldHaveCorrectSuffix() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..config..")
            .should().haveSimpleNameEndingWith("Config")
            .orShould().haveSimpleNameEndingWith("Configuration")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Exceptions must end with Exception")
    fun exceptionsShouldEndWithException() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..exception..")
            .should().haveSimpleNameEndingWith("Exception")
        
        rule.check(importedClasses)
    }
}
