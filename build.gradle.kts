plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kotlin.noarg) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    jacoco
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
}

allprojects {
    group = "com.adsearch"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
