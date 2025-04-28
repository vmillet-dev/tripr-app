package com.adsearch.architecture

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@AnalyzeClasses(packages = ["com.adsearch"], importOptions = [DoNotIncludeTests::class])
class GenericRulesTest {

    @Test
    @DisplayName("Abstract classes must either be used in inheritance or marked as abstract")
    fun abstractClassesMustBeUsedOrMarkedAsAbstract() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val abstractClasses = importedClasses.filter { it.modifiers.contains(JavaModifier.ABSTRACT) && !it.isInterface }
        
        for (abstractClass in abstractClasses) {
            val isExtended = importedClasses.any { 
                it.superclass.isPresent && it.superclass.get().name == abstractClass.name 
            }
            
            assert(isExtended || abstractClass.simpleName.startsWith("Abstract")) { 
                "Abstract class ${abstractClass.name} should either be extended or have a name starting with 'Abstract'" 
            }
        }
    }

    @Test
    @DisplayName("Data classes must not contain business logic")
    fun dataClassesMustNotContainBusinessLogic() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val dataClasses = importedClasses.filter { 
            it.annotations.any { annotation -> annotation.type.name == "kotlin.data" }
        }
        
        for (dataClass in dataClasses) {
            val methods = dataClass.methods.filter { 
                !it.name.startsWith("get") && 
                !it.name.startsWith("set") && 
                !it.name.startsWith("component") && 
                !it.name.equals("copy") &&
                !it.name.equals("equals") &&
                !it.name.equals("hashCode") &&
                !it.name.equals("toString")
            }
            
            assert(methods.isEmpty()) { 
                "Data class ${dataClass.name} should not contain business logic methods" 
            }
        }
    }

    @Test
    @DisplayName("Interfaces must not have an 'I' prefix")
    fun interfacesMustNotHaveIPrefix() {
        val importedClasses = ClassFileImporter().importPackages("com.adsearch")
        val rule = noClasses()
            .that().areInterfaces()
            .should().haveSimpleNameStartingWith("I")
        
        rule.check(importedClasses)
    }
}
