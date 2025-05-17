plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.noarg)
    alias(libs.plugins.spring.dependency.management)
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
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":common"))
    
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.bundles.springboot)
    implementation(libs.auth0.java.jwt)
    implementation(libs.konvert.api)
    implementation(libs.konvert.spring)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    
    ksp(libs.ksp.konvert)
    ksp(libs.ksp.konvert.spring)
    
    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = true
}
