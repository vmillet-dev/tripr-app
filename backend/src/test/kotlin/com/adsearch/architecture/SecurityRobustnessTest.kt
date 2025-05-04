package com.adsearch.architecture

import jakarta.validation.Valid
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class SecurityRobustnessTest : BaseArchitecture() {

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
}
