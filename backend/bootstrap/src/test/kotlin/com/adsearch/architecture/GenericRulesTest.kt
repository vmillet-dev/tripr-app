package com.adsearch.architecture

import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.Calendar
import java.util.Date

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

    @Test
    @DisplayName("No classes should use Date ou Calendar directly")
    fun noClassesShouldUseJavaUtilDate() {
        val rule = noClasses()
            .should().accessClassesThat().belongToAnyOf(Date::class.java, Calendar::class.java)
            .because("java.time API should be used instead of legacy Date")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Classes should not directly use System.out or System.err")
    fun classesShouldNotUseSystemOut() {
        val rule = noClasses()
            .should().accessField(System::class.java, "out")
            .orShould().accessField(System::class.java, "err")
            .because("Proper logging should be used instead of System.out/err")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("No classes should throw generic exceptions")
    fun noClassesShouldThrowGenericExceptions() {
        val genericExceptions = listOf(
            Exception::class.java,
            RuntimeException::class.java,
            Throwable::class.java
        )

        val rule = noMethods()
            .should().declareThrowableOfType(genericExceptions[0])
            .orShould().declareThrowableOfType(genericExceptions[1])
            .orShould().declareThrowableOfType(genericExceptions[2])
            .because("Specific exceptions should be used rather than generic ones")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("No use of @Autowired on fields")
    fun noUseOfAutowiredOnFields() {
        // No fields should be annotated with @Autowired
        val noSpringFieldInjection = noFields()
            .should().beAnnotatedWith(Autowired::class.java)

        // No methods should be annotated with @Autowired
        val noSpringMethodInjection = noMethods()
            .should().beAnnotatedWith(Autowired::class.java)

        // Check all rules
        noSpringFieldInjection.check(importedClasses)
        noSpringMethodInjection.check(importedClasses)
    }
}
