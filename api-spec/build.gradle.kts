// api-spec/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("org.openapi.generator") version "7.19.0"
}

val openapiSpecFile = "$projectDir/src/main/openapi/auth.yaml"
val generatedSourcesDir = "${layout.buildDirectory.get()}/generated"

// Configuration pour générer le serveur (backend Kotlin/Spring)
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set(openapiSpecFile)
    outputDir.set("$generatedSourcesDir/server")
    apiPackage.set("com.adsearch.infrastructure.adapter.in.rest")
    modelPackage.set("com.adsearch.infrastructure.adapter.in.rest.dto")
    invokerPackage.set("com.adsearch.infrastructure.config")
    configOptions.set(
        mapOf(
            "delegatePattern" to "true",
            "dateLibrary" to "java8",
            "interfaceOnly" to "true",
            "useTags" to "true",
            "performBeanValidation" to "true",
            "useSpringBoot3" to "true",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "UPPERCASE",
            "documentationProvider" to "springdoc"
        )
    )
}

tasks.register("fixGeneratedCode") {
    doLast {
        fileTree("$generatedSourcesDir/server/src/main/kotlin").matching { include("**/*.kt") }.forEach { file ->
            var content = file.readText()
            content = content.replace("adapter.in.", "adapter.`in`.")

            if (file.name == "SpringDocConfiguration.kt" && !content.contains("@Profile")) {
                content = content.replace(
                    "import org.springframework.context.annotation.Configuration",
                    "import org.springframework.context.annotation.Configuration\nimport org.springframework.context.annotation.Profile"
                ).replace("@Configuration", "@Profile(\"dev\")\n@Configuration")
            }
            file.writeText(content)
        }
    }
}

tasks.named("openApiGenerate") {
    finalizedBy("fixGeneratedCode")
}

