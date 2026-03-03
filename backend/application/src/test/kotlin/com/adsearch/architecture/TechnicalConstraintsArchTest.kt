package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class TechnicalConstraintsArchTest : AbstractArchTest() {
    @Test
    fun `classes should not use System out or err`() {
        noClasses()
            .should()
            .callMethod(System::class.java, "out")
            .orShould()
            .callMethod(System::class.java, "err")
            .because("Use a proper logging framework instead of System.out or System.err")
            .check(importedClasses)
    }

    @Test
    fun `classes should not use java util logging`() {
        noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("java.util.logging..")
            .because("Use SLF4J with Logback instead of java.util.logging")
            .check(importedClasses)
    }

    @Test
    fun `classes should not use log4j directly`() {
        noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.apache.log4j..")
            .because("Use SLF4J abstraction instead of Log4j directly to keep logging implementation replaceable")
            .check(importedClasses)
    }

    @Test
    fun `classes should not use Thread directly`() {
        noClasses()
            .should()
            .dependOnClassesThat()
            .areAssignableTo(Thread::class.java)
            .because("Use Spring's task execution abstraction or coroutines instead of raw Threads")
            .check(importedClasses)
    }

    @Test
    fun `deprecated apis should not be used`() {
        noClasses()
            .should()
            .dependOnClassesThat()
            .areAnnotatedWith(Deprecated::class.java)
            .because("Deprecated APIs should not be used as they may be removed in future versions")
            .check(importedClasses)
    }

    @Test
    fun `interfaces should not have the I prefix`() {
        noClasses()
            .that()
            .areInterfaces()
            .should()
            .haveSimpleNameStartingWith("I")
            .because("Interfaces should not be prefixed with 'I', use meaningful names instead")
            .check(importedClasses)
    }

    @Test
    fun `no generic exceptions should be caught`() {
        noClasses()
            .should(
                object : ArchCondition<JavaClass>("not catch generic exceptions") {
                    override fun check(javaClass: JavaClass, events: ConditionEvents) {
                        javaClass.methods.forEach { method ->
                            method.tryCatchBlocks.forEach { tryCatchBlock ->
                                tryCatchBlock.caughtThrowables.forEach { throwable ->
                                    if (throwable.name in listOf(
                                            "java.lang.Exception",
                                            "java.lang.RuntimeException",
                                            "java.lang.Throwable"
                                        )
                                    ) {
                                        events.add(
                                            SimpleConditionEvent.violated(
                                                javaClass,
                                                "Method '${method.name}' in class '${javaClass.fullName}' catches generic exception '${throwable.name}'"
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
            .because("Catching generic exceptions hides bugs and makes error handling imprecise")
            .check(importedClasses)
    }
}
