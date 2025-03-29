package com.adsearch.architecture

import com.adsearch.domain.exception.DomainException
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Tests to verify exception handling conventions
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ExceptionHandlingTest : BaseArchitectureTest() {
    
    @ArchTest
    val domainExceptionsShouldExtendDomainException: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Exception")
        .and().resideInAPackage("$DOMAIN_EXCEPTION_PACKAGE..")
        .and().doNotHaveSimpleName("DomainException")
        .should().beAssignableTo(DomainException::class.java)
        .because("Domain exceptions should extend DomainException")
    
    @ArchTest
    val restControllerAdviceClassesShouldExist: ArchRule = classes()
        .that().areAnnotatedWith(RestControllerAdvice::class.java)
        .should().haveSimpleNameEndingWith("ExceptionHandler")
        .because("Exception handlers should be centralized in classes with @RestControllerAdvice")
    
    @ArchTest
    val exceptionsShouldHaveExceptionSuffix: ArchRule = classes()
        .that().areAssignableTo(Exception::class.java)
        .and().resideInAPackage("$BASE_PACKAGE..")
        .should().haveSimpleNameEndingWith("Exception")
        .because("Exception classes should have 'Exception' suffix")
}
