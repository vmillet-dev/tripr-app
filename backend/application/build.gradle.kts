plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    api(project(":domain"))
    api(project(":infrastructure"))

    implementation(libs.springboot.logging)
    implementation(libs.springboot.autoconfigure)
    implementation(libs.springboot.webmvc)
    implementation(libs.springboot.security)
    implementation(libs.springboot.tomcat)
    implementation(libs.springboot.liquibase)
    implementation(libs.springboot.thymeleaf)
    implementation(libs.springboot.datajpa)
    implementation(libs.springboot.jdbc)
    implementation(libs.springboot.jackson)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    developmentOnly(libs.springboot.docker.compose)
    developmentOnly(libs.springboot.devtools)
    
    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("app.jar")
}

tasks.bootRun {
    enabled = true
    environment["SPRING_PROFILES_ACTIVE"] = environment["SPRING_PROFILES_ACTIVE"] ?: "dev"
    workingDir = project.rootDir.resolve("./../")
}
