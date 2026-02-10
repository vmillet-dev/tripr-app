package com.adsearch.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class NamingConventionTest : BaseArchitecture() {

    @Test
    @DisplayName("Classes in enum packages must end with Enum")
    fun enumClassesShouldEndWithEnum() {
        val rule = classes()
            .that().resideInAPackage("..enum..")
            .should().haveSimpleNameEndingWith("Enum")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Classes in dto packages must end with Dto")
    fun dtoClassesShouldEndWithDto() {
        val rule = classes()
            .that().resideInAPackage("..dto..")
            .should().haveSimpleNameEndingWith("Dto")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Classes in domain/model must end with Dom")
    fun domainModelClassesShouldEndWithDom() {
        val rule = classes()
            .that().resideInAPackage("..domain.model..")
            .and().areNotEnums()
            .should().haveSimpleNameEndingWith("Dom")
            .orShould().haveSimpleNameEndingWith("Companion")

        rule.check(importedClasses)
    }

    @Test
    @Disabled
    @DisplayName("Classes in domain/port must be interfaces ending with Port")
    fun domainPortClassesShouldBeInterfacesEndingWithPort() {
        val rule = classes()
            .that().resideInAPackage("..domain.port..")
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Port")

        rule.check(importedClasses)
    }

    @Test
    @Disabled
    @DisplayName("Controllers must end with Controller")
    fun primaryAdaptersShouldHaveCorrectSuffix() {
        val rule = classes()
            .that().resideInAPackage("..controller")
            .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("Companion")

        rule.check(importedClasses)
    }

    @Test
    @Disabled
    @DisplayName("Use cases must end with UseCase")
    fun useCasesShouldHaveCorrectSuffix() {
        val rule = classes()
            .that().resideInAPackage("..application")
            .and().haveSimpleNameNotEndingWith("Impl")
            .should().haveSimpleNameEndingWith("UseCase")
            .orShould().haveSimpleNameEndingWith("Companion")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Mappers must end with Mapper")
    fun mappersShouldEndWithMapper() {
        val rule = classes()
            .that().resideInAPackage("..mapper..")
            .should().haveSimpleNameEndingWith("Mapper")
            .orShould().haveSimpleNameEndingWith("MapperImpl")
            .orShould().haveSimpleNameEndingWith("DefaultImpls")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Configurations must end with Config")
    fun configurationsShouldHaveCorrectSuffix() {
        val rule = classes()
            .that().resideInAPackage("..config..")
            .should().haveSimpleNameEndingWith("Config")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Exceptions must end with Exception")
    fun exceptionsShouldEndWithException() {
        val rule = classes()
            .that().resideInAPackage("..exception..")
            .should().haveSimpleNameEndingWith("Exception")

        rule.check(importedClasses)
    }
}
