package com.adsearch.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class HexagonalArchitectureTest {

    @Test
    @DisplayName("Primary ports (input interfaces) must be implemented by use cases")
    fun primaryPortsMustBeImplementedByUseCases() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val interfaces = importedClasses.filter { 
            it.packageName.contains(".application.") && it.isInterface 
        }
        
        for (interfaceClass in interfaces) {
            val implementers = importedClasses.filter { 
                !it.isInterface && it.interfaces.contains(interfaceClass) 
            }
            
            val hasImplementationInApplicationImpl = implementers.any { 
                it.packageName.contains(".application.impl.") 
            }
            
            assert(hasImplementationInApplicationImpl) { 
                "Interface ${interfaceClass.name} should be implemented by a class in application.impl package" 
            }
        }
    }

    @Test
    @DisplayName("Secondary ports (output interfaces) must be implemented by secondary adapters")
    fun secondaryPortsMustBeImplementedBySecondaryAdapters() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val interfaces = importedClasses.filter { 
            it.packageName.contains(".domain.port.spi.") && it.isInterface 
        }
        
        for (interfaceClass in interfaces) {
            val implementers = importedClasses.filter { 
                !it.isInterface && it.interfaces.contains(interfaceClass) 
            }
            
            val hasImplementationInAdapterOut = implementers.any { 
                it.packageName.contains(".adapter.out.") 
            }
            
            assert(hasImplementationInAdapterOut) { 
                "Interface ${interfaceClass.name} should be implemented by a class in adapter.out package" 
            }
        }
    }

    @Test
    @DisplayName("All ports must be interfaces")
    fun allPortsMustBeInterfaces() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = classes()
            .that().resideInAPackage("..port..")
            .should().beInterfaces()
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Primary ports must only be used by primary adapters")
    fun primaryPortsMustOnlyBeUsedByPrimaryAdapters() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        // Find all primary port interfaces
        val primaryPorts = importedClasses.filter { 
            it.packageName.contains(".domain.port.api.") && it.isInterface 
        }
        
        // For each primary port, check that it's only used by primary adapters or application classes
        for (port in primaryPorts) {
            // Simplify dependency check by looking at class references
            val dependents = importedClasses.filter { clazz ->
                // Check if the class has fields, methods, or constructors that reference the port
                clazz.fields.any { it.type.name == port.name } ||
                clazz.methods.any { method -> 
                    method.returnType.name == port.name || 
                    method.parameters.any { param -> param.type.name == port.name }
                } ||
                clazz.constructors.any { constructor ->
                    constructor.parameters.any { param -> param.type.name == port.name }
                }
            }
            
            for (dependent in dependents) {
                val isInPrimaryAdapter = dependent.packageName.contains(".adapter.in.")
                val isInApplication = dependent.packageName.contains(".application.")
                
                assert(isInPrimaryAdapter || isInApplication) {
                    "Class ${dependent.name} should not depend on primary port ${port.name}"
                }
            }
        }
    }

    @Test
    @DisplayName("Secondary ports must only be used by the domain or application layer")
    fun secondaryPortsMustOnlyBeUsedByDomainOrApplication() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        // Find all secondary port interfaces
        val secondaryPorts = importedClasses.filter { 
            it.packageName.contains(".domain.port.spi.") && it.isInterface 
        }
        
        // For each secondary port, check that it's only used by domain or application classes
        for (port in secondaryPorts) {
            // Simplify dependency check by looking at class references
            val dependents = importedClasses.filter { clazz ->
                // Check if the class has fields, methods, or constructors that reference the port
                clazz.fields.any { it.type.name == port.name } ||
                clazz.methods.any { method -> 
                    method.returnType.name == port.name || 
                    method.parameters.any { param -> param.type.name == port.name }
                } ||
                clazz.constructors.any { constructor ->
                    constructor.parameters.any { param -> param.type.name == port.name }
                }
            }
            
            for (dependent in dependents) {
                val isInDomain = dependent.packageName.contains(".domain.")
                val isInApplication = dependent.packageName.contains(".application.")
                val isInAdapterOut = dependent.packageName.contains(".adapter.out.")
                
                assert(isInDomain || isInApplication || isInAdapterOut) {
                    "Class ${dependent.name} should not depend on secondary port ${port.name}"
                }
            }
        }
    }

    @Test
    @DisplayName("The domain must not expose its implementation details")
    fun domainMustNotExposeImplementationDetails() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        
        val domainClasses = importedClasses.filter { 
            it.packageName.contains(".domain.") &&
            !it.simpleName.endsWith("Dom") &&
            !it.simpleName.endsWith("Port") &&
            !it.simpleName.endsWith("Enum")
        }
        
        for (domainClass in domainClasses) {
            if (!domainClass.isInterface) {
                val isPublic = domainClass.modifiers.contains(JavaModifier.PUBLIC)
                assert(!isPublic) {
                    "Domain class ${domainClass.name} should not be public unless it ends with Dom, Port, or Enum"
                }
            }
        }
    }
}
