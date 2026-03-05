import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.kotlin.noarg) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kover)
}

subprojects {
    apply(plugin = rootProject.libs.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.libs.plugins.kover.get().pluginId)

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
        finalizedBy("koverHtmlReport", "koverXmlReport")
    }

    tasks.withType<BootRun> {
        enabled = false
    }

    tasks.withType<BootJar> {
        enabled = false
    }

    kover {
        reports {
            filters {
                excludes {
                    classes(
                        "**.config.**",
                        "**.dto.**",
                        "**.enums.**",
                        "**.exception.**",
                        "**.annotation.**",
                        "**.entity.**",
                        "**.model.**",
                        "**.*DefaultImpls",
                        "**.*Api",
                        "**.*Delegate",
                        "**.*ExceptionHandler",
                        "**.*ApiUtil",
                        "**.*Exception",
                        $$"**.*$Companion"
                    )
                }
            }
            total {
                xml {
                    onCheck = true
                }
                html {
                    onCheck = true
                }
            }
            verify {
                rule {
                    bound {
                        minValue = 80
                    }
                }
            }
        }
    }
}


