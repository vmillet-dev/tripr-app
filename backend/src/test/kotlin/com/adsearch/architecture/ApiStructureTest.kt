package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Tests to verify API endpoint structure
 */
@AnalyzeClasses(
    packages = ["com.adsearch"],
    importOptions = [ImportOption.DoNotIncludeTests::class]
)
class ApiStructureTest : BaseArchitectureTest() {
    
    @ArchTest
    val controllersShouldBeAnnotatedWithRestController: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Controller")
        .and().resideInAPackage("$INFRASTRUCTURE_WEB_PACKAGE.controller..")
        .should().beAnnotatedWith(RestController::class.java)
        .because("Controllers should be annotated with @RestController")
    
    @ArchTest
    val controllersShouldBeAnnotatedWithRequestMapping: ArchRule = classes()
        .that().areAnnotatedWith(RestController::class.java)
        .should().beAnnotatedWith(RequestMapping::class.java)
        .because("Controllers should be annotated with @RequestMapping")
    
    @ArchTest
    val controllersShouldBeAnnotatedWithTag: ArchRule = classes()
        .that().areAnnotatedWith(RestController::class.java)
        .should().beAnnotatedWith(Tag::class.java)
        .because("Controllers should be annotated with @Tag for Swagger documentation")
    
    @ArchTest
    val dtoClassesShouldBeInDtoPackage: ArchRule = classes()
        .that().haveSimpleNameEndingWith("Dto")
        .should().resideInAPackage("$INFRASTRUCTURE_WEB_PACKAGE.dto..")
        .because("DTO classes should be in the dto package")
}
