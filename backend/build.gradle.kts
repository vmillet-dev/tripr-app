plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.noarg)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.version.catalog.update)
    id("org.openrewrite.rewrite") version "6.10.0"
}

group = "com.adsearch"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly(libs.postgresql)

    developmentOnly(libs.springboot.docker.compose)
    developmentOnly(libs.springboot.devtools)

    implementation(libs.bundles.springboot) // Spring Boot bundles
    implementation(libs.liquibase.core)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.kotlinx.coroutines.slf4j)
    implementation(libs.logback.classic)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.auth0.java.jwt)

    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.apache.httpclient5)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)

}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "ch.qos.logback") {
            useVersion(libs.versions.logback.get())
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootRun {
    environment["SPRING_PROFILES_ACTIVE"] = environment["SPRING_PROFILES_ACTIVE"] ?: "dev"
    workingDir = project.rootDir.resolve("./../")
}

// OpenRewrite configuration
rewrite {
    // Code formatting and cleanup recipes
    activeRecipe("org.openrewrite.java.format.AutoFormat")
    activeRecipe("org.openrewrite.java.RemoveUnusedImports")
    activeRecipe("org.openrewrite.java.OrderImports")
    activeRecipe("org.openrewrite.kotlin.format.AutoFormat")
    activeRecipe("org.openrewrite.kotlin.cleanup.EqualsMethodUsage")
    activeRecipe("org.openrewrite.kotlin.cleanup.ImplicitParameterInLambda")
    activeRecipe("org.openrewrite.kotlin.cleanup.RemoveTrailingComma")
    activeRecipe("org.openrewrite.kotlin.cleanup.RemoveTrailingSemicolon")
    activeRecipe("org.openrewrite.kotlin.cleanup.ReplaceCharToIntWithCode")
    activeRecipe("org.openrewrite.kotlin.cleanup.UnnecessaryTypeParentheses")
    
    // Spring-related recipes
    activeRecipe("org.openrewrite.java.spring.NoRequestMappingAnnotation")
    activeRecipe("org.openrewrite.java.spring.ImplicitWebAnnotationNames")
    activeRecipe("org.openrewrite.java.spring.boot3.SpringBoot3BestPractices")
    
    // Security-related recipes
    activeRecipe("org.openrewrite.java.security.SecureRandom")
    activeRecipe("org.openrewrite.java.security.JavaSecurityBestPractices")
    
    // Gradle-related recipes
    activeRecipe("org.openrewrite.gradle.UpdateGradleWrapper")
    activeRecipe("org.openrewrite.gradle.UpdateJavaCompatibility")
}

