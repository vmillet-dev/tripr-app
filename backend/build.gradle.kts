plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.adsearch"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly(libs.postgresql)

    developmentOnly(libs.springboot.docker.compose)
    developmentOnly(libs.springboot.devtools)

    implementation(libs.bundles.springboot) // Spring Boot bundles
    implementation(libs.micrometer.registry.prometheus)
    implementation(libs.liquibase.core)
    // Swagger UI
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    // Kotlin
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.kotlinx.coroutines.slf4j)
    // Logging
    implementation(libs.logback.classic)
    implementation(libs.janino)
    // JWT Authentication
    implementation(libs.auth0.java.jwt)

    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.junit.platform.launcher)
    // TestContainers
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    // ArchUnit
    testImplementation(libs.archunit.junit5)
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
//    workingDir = project.rootDir.resolve("./../")
}

