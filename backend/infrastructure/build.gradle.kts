plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.noarg)
    alias(libs.plugins.kotlin.kapt)
}

dependencies {
    implementation(project(":backend:domain"))

    runtimeOnly(libs.postgresql)

    implementation(libs.springboot.security)
    implementation(libs.springboot.tomcat)
    implementation(libs.springboot.datajpa)
    implementation(libs.springboot.thymeleaf)
    implementation(libs.springboot.mail)

    implementation(libs.kotlin.reflect)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(libs.auth0.java.jwt)
    implementation(libs.mapstruct.api)

    kapt(libs.mapstruct.kapt)
}

sourceSets {
    main {
        kotlin {
            srcDir("${project(":api-spec").buildDir}/generated/server/src/main/kotlin")
        }
    }
}
