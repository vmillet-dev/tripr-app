plugins {
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    api(project(":domain"))
    api(project(":application"))
    api(project(":common"))
    api(project(":infrastructure"))

    implementation(libs.springboot.starter.web)
    implementation(libs.liquibase.core)

    developmentOnly(libs.springboot.docker.compose)
    developmentOnly(libs.springboot.devtools)

    testImplementation(libs.springboot.starter.test)
    testImplementation(libs.springboot.starter.security)
    testImplementation(libs.springboot.starter.validation)
    testImplementation(libs.kotlin.test.junit5)
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

tasks.bootJar {
    enabled = true
    archiveFileName.set("app.jar")
}

tasks.bootRun {
    enabled = true
    environment["SPRING_PROFILES_ACTIVE"] = environment["SPRING_PROFILES_ACTIVE"] ?: "dev"
    workingDir = project.rootDir.resolve("./../")
}
