package com.adsearch.architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractArchTest {

    protected val basePackage = "com.adsearch"
    protected val domainPackage = "$basePackage.domain"
    protected val applicationPackage = "$basePackage.application"
    protected val infrastructurePackage = "$basePackage.infrastructure"

    protected val importedClasses: JavaClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages(basePackage)
        .that(
            object : DescribedPredicate<JavaClass>("is not a Kotlin generated class") {
                override fun test(javaClass: JavaClass): Boolean {
                    return !javaClass.fullName.contains("$") &&
                        !javaClass.simpleName.endsWith("Impl") &&
                        javaClass.simpleName != "DefaultImpls"
                }
            }
        )
}
