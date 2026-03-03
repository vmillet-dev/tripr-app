package com.adsearch.architecture

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.Test

class NamingConventionArchTest : AbstractArchTest() {

    @Test
    fun `domain port in should only contain interfaces ending with UseCase`() {
        classes()
            .that()
            .resideInAPackage("$domainPackage.port.in..")
            .should()
            .beInterfaces()
            .andShould()
            .haveSimpleNameEndingWith("UseCase")
            .because("All classes in domain/port/in must be interfaces ending with 'UseCase'")
            .check(importedClasses)
    }

    @Test
    fun `domain port out should only contain interfaces ending with Port`() {
        classes()
            .that()
            .resideInAPackage("$domainPackage.port.out..")
            .should()
            .beInterfaces()
            .andShould()
            .haveSimpleNameEndingWith("Port")
            .because("All classes in domain/port/out must be interfaces ending with 'Port'")
            .check(importedClasses)
    }

    @Test
    fun `domain services should be named with Service suffix`() {
        classes()
            .that()
            .resideInAPackage("$domainPackage.service..")
            .should()
            .haveSimpleNameEndingWith("Service")
            .because("All classes in domain/service must end with 'Service'")
            .check(importedClasses)
    }

    @Test
    fun `enums packages should only contain enums ending with Enum`() {
        classes()
            .that()
            .resideInAPackage("..enums..")
            .should()
            .beEnums()
            .andShould()
            .haveSimpleNameEndingWith("Enum")
            .because("All types in 'enums' packages must be enums and end with 'Enum'")
            .check(importedClasses)
    }

    @Test
    fun `entity packages should only contain classes ending with Entity`() {
        classes()
            .that()
            .resideInAPackage("..entity..")
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .should()
            .haveSimpleNameEndingWith("Entity")
            .because("All classes in 'entity' packages must end with 'Entity'")
            .check(importedClasses)
    }

    @Test
    fun `jpa packages should only contain classes ending with Repository`() {
        classes()
            .that()
            .resideInAPackage("..jpa..")
            .and()
            .areInterfaces()
            .should()
            .haveSimpleNameEndingWith("Repository")
            .because("All classes in 'jpa' packages must end with 'Repository'")
            .check(importedClasses)
    }

    @Test
    fun `mapper packages should only contain classes ending with Mapper`() {
        classes()
            .that()
            .resideInAPackage("..mapper..")
            .and()
            .areInterfaces()
            .should()
            .haveSimpleNameEndingWith("Mapper")
            .because("All classes in 'mapper' packages must end with 'Mapper'")
            .check(importedClasses)
    }

    @Test
    fun `config packages should only contain classes ending with Config`() {
        classes()
            .that()
            .resideInAPackage("..config..")
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .should()
            .haveSimpleNameEndingWith("Config")
            .orShould()
            .haveSimpleNameEndingWith("Configuration")
            .because("All classes in 'config' packages must end with 'Config'")
            .check(importedClasses)
    }

    @Test
    fun `domain exceptions should end with Exception`() {
        classes()
            .that()
            .resideInAPackage("..exception")
            .should()
            .haveSimpleNameEndingWith("Exception")
            .because("All classes in domain/exceptions must end with 'Exception'")
            .check(importedClasses)
    }
}
