package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GenericRulesTest : BaseArchitecture() {

    @Test
    @DisplayName("Abstract classes must either be used in inheritance or marked as abstract")
    fun abstractClassesMustBeUsedOrMarkedAsAbstract() {
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
    @DisplayName("Interfaces must not have an 'I' prefix")
    fun interfacesMustNotHaveIPrefix() {
        val rule = noClasses()
            .that().areInterfaces()
            .should().haveSimpleNameStartingWith("I")

        rule.check(importedClasses)
    }
}
