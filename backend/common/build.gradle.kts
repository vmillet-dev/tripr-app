plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.dependency.management)
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
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.bundles.springboot)
    
    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.kotlin.test.junit5)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    enabled = true
}
