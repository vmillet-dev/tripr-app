plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.noarg)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ksp)
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
    ksp(libs.ksp.konvert)
    ksp(libs.ksp.konvert.spring)

    runtimeOnly(libs.postgresql)

    developmentOnly(libs.springboot.docker.compose)
    developmentOnly(libs.springboot.devtools)

    implementation(libs.bundles.springboot) // Spring Boot bundles
    implementation(libs.liquibase.core)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.auth0.java.jwt)
    implementation(libs.konvert.api)
    implementation(libs.konvert.spring)

    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.apache.httpclient5)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)

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

