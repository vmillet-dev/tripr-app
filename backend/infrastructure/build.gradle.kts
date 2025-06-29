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

    implementation(libs.bundles.springboot) // springboot bundle

    implementation(libs.kotlin.reflect)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.auth0.java.jwt)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.mapstruct.api)

    kapt(libs.mapstruct.kapt)
}
