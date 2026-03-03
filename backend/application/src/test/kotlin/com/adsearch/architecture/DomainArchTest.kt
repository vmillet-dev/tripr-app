package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class DomainArchTest : AbstractArchTest() {

    @Test
    fun `domain should not depend on spring or any frameworks`() {
        noClasses()
            .that()
            .resideInAPackage("$domainPackage..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "jakarta..",
                "javax.."
            )
            .because("Domain layer must be framework-agnostic and only use pure Java/Kotlin")
            .check(importedClasses)
    }

    @Test
    fun `domain should not depend on application or infrastructure packages`() {
        noClasses()
            .that()
            .resideInAPackage("$domainPackage..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                "$applicationPackage..",
                "$infrastructurePackage.."
            )
            .because("Domain layer must not depend on application or infrastructure layers")
            .check(importedClasses)
    }


    @Test
    fun `domaine services should only implement interfaces from api package`() {

        val onlyImplementApiInterfaces = object : ArchCondition<JavaClass>(
            "only implement interfaces from $domainPackage.port.in"
        ) {
            override fun check(item: JavaClass, events: ConditionEvents) {
                val forbiddenInterfaces = item.interfaces.filterNot { it.name.startsWith("$domainPackage.port.in") }
                if (forbiddenInterfaces.isNotEmpty()) {
                    events.add(
                        SimpleConditionEvent.violated(
                            item,
                            "${item.name} implements forbidden interfaces: ${
                                forbiddenInterfaces.joinToString { it.name }
                            }"
                        )
                    )
                }
            }
        }

        classes()
            .that().resideInAPackage("$domainPackage.service..")
            .should(onlyImplementApiInterfaces)
            .check(importedClasses)
    }

    @Test
    fun `domain services should only use port out interfaces in their constructors`() {
        classes()
            .that()
            .resideInAPackage("$domainPackage.service..")
            .should(
                object : ArchCondition<JavaClass>("use only domain/port/out interfaces in constructors") {
                    override fun check(javaClass: JavaClass, events: ConditionEvents) {
                        javaClass.constructors.forEach { constructor ->
                            constructor.parameterTypes.forEach { paramType ->
                                val isPortOutInterface = paramType.name.startsWith("$domainPackage.port.out")
                                val isJavaLang = paramType.name.startsWith("java.")

                                if (!isPortOutInterface && !isJavaLang) {
                                    events.add(
                                        SimpleConditionEvent.violated(
                                            javaClass,
                                            "Constructor parameter '${paramType.name}' in '${javaClass.name}' is not an interface from domain/port/out"
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            )
            .because("Service constructors must only use interfaces from domain/port/out as dependencies")
            .check(importedClasses)
    }

    @Test
    fun `domain exceptions should extend Exception`() {
        classes()
            .that()
            .resideInAPackage("$domainPackage.exception..")
            .and()
            .resideOutsideOfPackage("$domainPackage.exception.enums..")
            .should()
            .beAssignableTo(Exception::class.java)
            .because("Domain exceptions should extend RuntimeException to avoid checked exceptions boilerplate")
            .check(importedClasses)
    }

    @Test
    fun `domain classes should not implement Serializable`() {
        noClasses()
            .that()
            .resideInAPackage("$domainPackage..")
            .and()
            .resideOutsideOfPackage("$domainPackage.enums..")
            .and()
            .resideOutsideOfPackage("$domainPackage.exception..")
            .should()
            .implement(java.io.Serializable::class.java)
            .because("Domain classes should not implement Serializable as it couples them to Java serialization mechanism")
            .check(importedClasses)
    }
}
