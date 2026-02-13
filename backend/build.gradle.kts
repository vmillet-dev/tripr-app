import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

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

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = "jacoco")

    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
                "-Werror",
                "-Wextra",
                "-Xannotation-default-target=param-property"
            )
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<BootRun> {
        enabled = false
    }

    tasks.withType<BootJar> {
        enabled = false
    }

    jacoco {
        toolVersion = "0.8.14"
    }

    tasks.test {
        finalizedBy("jacocoTestReport")
    }

    afterEvaluate {
        tasks.named<JacocoReport>("jacocoTestReport") {
            dependsOn(tasks.test)

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }

            classDirectories.setFrom(
                fileTree("build/classes/kotlin/main") {
                    exclude(
                        "**/config/**",
                        "**/dto/**",
                        "**/enum/**",
                        "**/annotation/**"
                    )
                }
            )

            sourceDirectories.setFrom(files("src/main/kotlin"))
            executionData.setFrom(files("build/jacoco/test.exec"))
        }
    }
}


