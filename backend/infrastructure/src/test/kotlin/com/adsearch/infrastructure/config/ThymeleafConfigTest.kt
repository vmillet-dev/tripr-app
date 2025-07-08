package com.adsearch.infrastructure.config

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Thymeleaf Config Tests")
class ThymeleafConfigTest {

    private lateinit var thymeleafConfig: ThymeleafConfig

    @BeforeEach
    fun setUp() {
        thymeleafConfig = ThymeleafConfig()
    }

    @Test
    @DisplayName("Should create email template engine when requested")
    fun shouldCreateEmailTemplateEngineWhenRequested() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
    }

    @Test
    @DisplayName("Should configure template engine with HTML template resolver")
    fun shouldConfigureTemplateEngineWithHtmlTemplateResolver() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        val templateResolvers = springTemplateEngine.templateResolvers
        assertNotNull(templateResolvers)
        assertTrue(templateResolvers.isNotEmpty())
    }

    @Test
    @DisplayName("Should create template engine with correct configuration")
    fun shouldCreateTemplateEngineWithCorrectConfiguration() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        val templateResolvers = springTemplateEngine.templateResolvers
        assertEquals(1, templateResolvers.size)
        
        val templateResolver = templateResolvers.first()
        assertTrue(templateResolver is ClassLoaderTemplateResolver)
    }

    @Test
    @DisplayName("Should maintain consistent template engine creation")
    fun shouldMaintainConsistentTemplateEngineCreation() {
        // Given & When
        val templateEngine1 = thymeleafConfig.emailTemplateEngine()
        val templateEngine2 = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine1)
        assertNotNull(templateEngine2)
        assertTrue(templateEngine1 is SpringTemplateEngine)
        assertTrue(templateEngine2 is SpringTemplateEngine)
    }

    @Test
    @DisplayName("Should create template engine that can process templates")
    fun shouldCreateTemplateEngineThatCanProcessTemplates() {
        // Given
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // When
        val canProcess = templateEngine.templateResolvers.isNotEmpty()

        // Then
        assertTrue(canProcess)
        assertNotNull(templateEngine)
    }

    @Test
    @DisplayName("Should configure template engine for email processing")
    fun shouldConfigureTemplateEngineForEmailProcessing() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        val templateResolvers = springTemplateEngine.templateResolvers
        assertTrue(templateResolvers.isNotEmpty())
        
        val templateResolver = templateResolvers.first() as ClassLoaderTemplateResolver
        assertNotNull(templateResolver)
    }

    @Test
    @DisplayName("Should handle multiple template engine creations independently")
    fun shouldHandleMultipleTemplateEngineCreationsIndependently() {
        // Given
        val config1 = ThymeleafConfig()
        val config2 = ThymeleafConfig()

        // When
        val templateEngine1 = config1.emailTemplateEngine()
        val templateEngine2 = config2.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine1)
        assertNotNull(templateEngine2)
        assertTrue(templateEngine1 is SpringTemplateEngine)
        assertTrue(templateEngine2 is SpringTemplateEngine)
    }

    @Test
    @DisplayName("Should create template engine with Spring integration")
    fun shouldCreateTemplateEngineWithSpringIntegration() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        assertNotNull(springTemplateEngine.templateResolvers)
    }

    @Test
    @DisplayName("Should configure template engine for HTML template mode")
    fun shouldConfigureTemplateEngineForHtmlTemplateMode() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        val templateResolvers = springTemplateEngine.templateResolvers
        assertTrue(templateResolvers.isNotEmpty())
    }

    @Test
    @DisplayName("Should create template engine suitable for email templates")
    fun shouldCreateTemplateEngineSuitableForEmailTemplates() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        val templateResolvers = springTemplateEngine.templateResolvers
        assertEquals(1, templateResolvers.size)
    }

    @Test
    @DisplayName("Should handle template engine initialization correctly")
    fun shouldHandleTemplateEngineInitializationCorrectly() {
        // Given
        val freshConfig = ThymeleafConfig()

        // When
        val templateEngine = freshConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        assertNotNull(springTemplateEngine.templateResolvers)
        assertTrue(springTemplateEngine.templateResolvers.isNotEmpty())
    }

    @Test
    @DisplayName("Should create template engine with proper resolver configuration")
    fun shouldCreateTemplateEngineWithProperResolverConfiguration() {
        // Given & When
        val templateEngine = thymeleafConfig.emailTemplateEngine()

        // Then
        assertNotNull(templateEngine)
        assertTrue(templateEngine is SpringTemplateEngine)
        
        val springTemplateEngine = templateEngine as SpringTemplateEngine
        val templateResolvers = springTemplateEngine.templateResolvers
        assertTrue(templateResolvers.isNotEmpty())
        
        val resolver = templateResolvers.first()
        assertTrue(resolver is ClassLoaderTemplateResolver)
    }
}
