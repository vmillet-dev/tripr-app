package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import jakarta.validation.Valid
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

class SecurityRobustnessTest : BaseArchitecture() {

    private val requestMappingAnnotations = setOf(
        GetMapping::class.java,
        PostMapping::class.java,
        PutMapping::class.java,
        DeleteMapping::class.java,
        PatchMapping::class.java,
        RequestMapping::class.java
    )

    @Test
    @DisplayName("API endpoints must have appropriate validation")
    fun apiEndpointsMustHaveAppropriateValidation() {
        // Check that controller methods with request bodies use @Valid
        val controllerClasses = importedClasses.filter {
            it.simpleName.endsWith("Controller") && !it.isInterface
        }

        for (controllerClass in controllerClasses) {
            val methods = controllerClass.methods.filter {
                it.parameters.any { param ->
                    param.annotations.any { anno ->
                        anno.type.name.contains("RequestBody")
                    }
                }
            }

            for (method in methods) {
                val hasValidAnnotation = method.parameters.any { param ->
                    param.annotations.any { anno ->
                        anno.type.name == Valid::class.java.name
                    }
                }

                assert(hasValidAnnotation) {
                    "Controller method ${controllerClass.name}.${method.name} with @RequestBody should use @Valid"
                }
            }
        }
    }

    @Test
    @DisplayName("DTOs must use validation annotations")
    fun dtosMustUseValidationAnnotations() {
        val dtoClasses = importedClasses.filter {
            it.simpleName.endsWith("Dto") && !it.isInterface
        }

        for (dtoClass in dtoClasses) {
            val hasValidationAnnotations = dtoClass.fields.any { field ->
                field.annotations.any { anno ->
                    anno.type.name.startsWith("jakarta.validation.constraints")
                }
            }

            assert(hasValidationAnnotations) {
                "DTO class ${dtoClass.name} should use validation annotations"
            }
        }
    }

    @Test
    @DisplayName("REST controllers must have @PreAuthorize either at class level or on all mapped methods")
    fun restControllersMustHavePreAuthorize() {
        val condition = object : ArchCondition<JavaClass>("have class-level or per-method @PreAuthorize") {
            override fun check(javaClass: JavaClass, events: ConditionEvents) {
                if (!javaClass.isAnnotatedWith(RestController::class.java)) return

                val hasClassPreAuth = javaClass.isAnnotatedWith(PreAuthorize::class.java)

                if (!hasClassPreAuth) {
                    javaClass.methods
                        .filter { method -> method.annotations.any { it.rawType.reflect() in requestMappingAnnotations } }
                        .forEach { method ->
                            if (!method.isAnnotatedWith(PreAuthorize::class.java)) {
                                events.add(
                                    SimpleConditionEvent.violated(
                                        method,
                                        "Method ${method.fullName} in @RestController ${javaClass.name} " +
                                            "is mapped but missing @PreAuthorize"
                                    )
                                )
                            }
                        }
                }
            }
        }

        classes()
            .that().areAnnotatedWith(RestController::class.java)
            .should(condition)
            .because("REST endpoints must be secured with @PreAuthorize at either class or method level")
            .check(importedClasses)
    }
}
