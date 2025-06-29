plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.noarg)
    alias(libs.plugins.kotlin.kapt)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":common"))

    runtimeOnly(libs.postgresql)

    implementation(libs.springboot.starter.web)
    implementation(libs.springboot.starter.data.jpa)
    implementation(libs.springboot.starter.security)
    implementation(libs.springboot.starter.mail)
    implementation(libs.springboot.starter.thymeleaf)
    implementation(libs.springboot.starter.validation)

    implementation(libs.kotlin.reflect)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.auth0.java.jwt)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.mapstruct.api)

    kapt(libs.mapstruct.kapt)
}

kapt {
    keepJavacAnnotationProcessors = true
}
