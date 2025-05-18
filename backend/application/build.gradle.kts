plugins {
    alias(libs.plugins.kotlin.spring)
}

dependencies {
    api(project(":domain"))
    api(project(":common"))

    implementation(libs.springboot.starter.web)
}
