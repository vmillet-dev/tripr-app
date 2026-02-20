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
        finalizedBy("jacocoTestReport")
    }

    tasks.withType<BootRun> {
        enabled = false
    }

    tasks.withType<BootJar> {
        enabled = false
    }

    jacoco {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    afterEvaluate {
        tasks.named<JacocoReport>("jacocoTestReport") {
            dependsOn(tasks.test)

            reports {
                xml.required.set(true)
            }

            classDirectories.setFrom(
                fileTree("build/classes/kotlin/main") {
                    exclude(
                        "**/config/**",
                        "**/dto/**",
                        "**/enums/**",
                        "**/exception/**",
                        "**/annotation/**"
                    )
                }
            )
        }
    }
}


