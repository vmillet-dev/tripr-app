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
    implementation(libs.auth0.java.jwt)
    implementation(libs.mapstruct.api)
    implementation(libs.jackson.module)
    implementation(libs.springdoc.openapi.starter.webmvc.ui) {
        exclude(group = "tools.jackson.module", module = "jackson-module-kotlin")
    }
    kapt(libs.mapstruct.kapt)
}

tasks.matching { it.name == "kaptGenerateStubsKotlin" }.configureEach {
    dependsOn(":api-spec:openApiGenerate")
}

sourceSets {
    main {
        kotlin {
            srcDir("${project(":api-spec").layout.buildDirectory.get()}/generated/server/src/main/kotlin")
        }
    }
}
