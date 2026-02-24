plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.openapi)
}

val generatedSourcesDir = "${layout.buildDirectory.get()}/generated"

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/src/main/openapi/auth.yaml")
    outputDir.set("$generatedSourcesDir/server")
    apiPackage.set("com.adsearch.infrastructure.adapter.in.rest")
    modelPackage.set("com.adsearch.infrastructure.adapter.in.rest.dto")
    invokerPackage.set("com.adsearch.config")
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
            file.writeText(content)
        }
    }
}

tasks.named("openApiGenerate") {
    finalizedBy("fixGeneratedCode")
}

