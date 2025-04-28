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
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class SecurityRobustnessTest {

    @Test
    @DisplayName("API endpoints must have appropriate validation")
    fun apiEndpointsMustHaveAppropriateValidation() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
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
    @Disabled("Temporarily disabled - see DISABLED_TESTS.md")
    @DisplayName("DTOs must use validation annotations")
    fun dtosMustUseValidationAnnotations() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
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
