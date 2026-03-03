package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class InfrastructureArchTest : AbstractArchTest() {

    @Test
    fun `infrastructure adapter out should implement a port out interface and end with Adapter old`() {
        classes()
            .that()
            .resideInAPackage("$infrastructurePackage.adapter.out..")
            .and()
            .resideOutsideOfPackage("$infrastructurePackage.adapter.out.persistence.entity..")
            .and()
            .areNotInterfaces()
            .and()
            .areNotEnums()
            .should()
            .haveSimpleNameEndingWith("Adapter")
            .andShould()
            .implement(
                object : com.tngtech.archunit.base.DescribedPredicate<JavaClass>("interface from domain/port/out") {
                    override fun test(javaClass: JavaClass): Boolean {
                        return javaClass.packageName.startsWith("$domainPackage.port.out") && javaClass.isInterface
                    }
                }
            )
            .because("All classes in infrastructure/adapter/out must implement a domain/port/out interface and end with 'Adapter'")
            .check(importedClasses)
    }

    @Test
    fun `infrastructure adapter out should only implement a port out interface (1 level max)`() {
        val onlyImplementApiInterfaces = object : ArchCondition<JavaClass>(
            "only implement interfaces from $domainPackage.port.out"
        ) {
            override fun check(item: JavaClass, events: ConditionEvents) {
                val forbiddenInterfaces = item.interfaces.filterNot { it.name.startsWith("$domainPackage.port.out") }
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
            .that().resideInAnyPackage(
                "$infrastructurePackage.adapter.out",
                "$infrastructurePackage.adapter.out.authentication",
                "$infrastructurePackage.adapter.out.notification",
                "$infrastructurePackage.adapter.out.persistence"
            )
            .or().resideInAnyPackage()
            .should()
            .haveSimpleNameEndingWith("Adapter")
            .andShould(onlyImplementApiInterfaces)
            .check(importedClasses)
    }

    @Test
    fun `infrastructure adapter out should not depend on infrastructure adapter in`() {
        noClasses()
            .that()
            .resideInAPackage("$infrastructurePackage.adapter.out..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("$infrastructurePackage.adapter.in..")
            .because("infrastructure/adapter/out must not depend on infrastructure/adapter/in")
            .check(importedClasses)
    }

    @Test
    fun `infrastructure adapter in should not depend on infrastructure adapter out`() {
        noClasses()
            .that()
            .resideInAPackage("$infrastructurePackage.adapter.in..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("$infrastructurePackage.adapter.out..")
            .because("infrastructure/adapter/in must not depend on infrastructure/adapter/out")
            .check(importedClasses)
    }

    @Test
    fun `controllers should only be in infrastructure adapter in`() {
        classes()
            .that()
            .areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should()
            .resideInAPackage("$infrastructurePackage.adapter.in..")
            .because("Controllers must reside in infrastructure/adapter/in to respect hexagonal architecture")
            .check(importedClasses)
    }

    @Test
    fun `Transactional annotation should only be used in infrastructure layer`() {
        classes()
            .that()
            .areAnnotatedWith("org.springframework.transaction.annotation.Transactional")
            .should()

            .resideInAPackage("$infrastructurePackage..")
            .because("@Transactional should only be used in the infrastructure layer")
            .allowEmptyShould(true)
            .check(importedClasses)
    }

    @Test
    fun `no field injection should be used`() {
        noClasses()
            .should(
                object : ArchCondition<JavaClass>("not use field injection") {
                    override fun check(javaClass: JavaClass, events: ConditionEvents) {
                        javaClass.fields
                            .filter { field ->
                                field.isAnnotatedWith("org.springframework.beans.factory.annotation.Autowired") ||
                                    field.isAnnotatedWith("jakarta.inject.Inject")
                            }
                            .forEach { field ->
                                events.add(
                                    SimpleConditionEvent.violated(
                                        javaClass,
                                        "Field '${field.name}' in class '${javaClass.fullName}' uses field injection"
                                    )
                                )
                            }
                    }
                }
            )
            .because("Use constructor injection instead of field injection for better testability and immutability")
            .check(importedClasses)
    }
}
