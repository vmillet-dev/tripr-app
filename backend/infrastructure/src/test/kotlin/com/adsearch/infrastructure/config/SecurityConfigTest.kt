package com.adsearch.infrastructure.config

import com.adsearch.infrastructure.adapter.`in`.security.JwtAuthenticationFilter
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@DisplayName("Security Config Tests")
class SecurityConfigTest {

    private lateinit var securityConfig: SecurityConfig
    private val jwtAuthenticationFilter = mockk<JwtAuthenticationFilter>()

    @BeforeEach
    fun setUp() {
        securityConfig = SecurityConfig(jwtAuthenticationFilter)
    }


    @Test
    @DisplayName("Should create CORS configuration source with correct origins")
    fun shouldCreateCorsConfigurationSourceWithCorrectOrigins() {
        // Given & When
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // Then
        assertNotNull(corsConfigurationSource)
        
        val request = MockHttpServletRequest()
        request.requestURI = "/**"
        val corsConfiguration = corsConfigurationSource.getCorsConfiguration(request)
        assertNotNull(corsConfiguration)
        assertEquals(3, corsConfiguration!!.allowedOrigins!!.size)
        assertTrue(corsConfiguration.allowedOrigins!!.contains("http://localhost:4200"))
        assertTrue(corsConfiguration.allowedOrigins!!.contains("http://localhost:8080"))
        assertTrue(corsConfiguration.allowedOrigins!!.contains("http://localhost:8081"))
    }

    @Test
    @DisplayName("Should create CORS configuration source with correct methods")
    fun shouldCreateCorsConfigurationSourceWithCorrectMethods() {
        // Given & When
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // Then
        assertNotNull(corsConfigurationSource)
        
        val request = MockHttpServletRequest()
        request.requestURI = "/**"
        val corsConfiguration = corsConfigurationSource.getCorsConfiguration(request)
        assertNotNull(corsConfiguration)
        assertEquals(5, corsConfiguration!!.allowedMethods!!.size)
        assertTrue(corsConfiguration.allowedMethods!!.contains("GET"))
        assertTrue(corsConfiguration.allowedMethods!!.contains("POST"))
        assertTrue(corsConfiguration.allowedMethods!!.contains("PUT"))
        assertTrue(corsConfiguration.allowedMethods!!.contains("DELETE"))
        assertTrue(corsConfiguration.allowedMethods!!.contains("OPTIONS"))
    }

    @Test
    @DisplayName("Should create CORS configuration source with credentials allowed")
    fun shouldCreateCorsConfigurationSourceWithCredentialsAllowed() {
        // Given & When
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // Then
        assertNotNull(corsConfigurationSource)
        
        val request = MockHttpServletRequest()
        request.requestURI = "/**"
        val corsConfiguration = corsConfigurationSource.getCorsConfiguration(request)
        assertNotNull(corsConfiguration)
        assertEquals(true, corsConfiguration!!.allowCredentials)
    }

    @Test
    @DisplayName("Should create CORS configuration source with correct max age")
    fun shouldCreateCorsConfigurationSourceWithCorrectMaxAge() {
        // Given & When
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // Then
        assertNotNull(corsConfigurationSource)
        
        val request = MockHttpServletRequest()
        request.requestURI = "/**"
        val corsConfiguration = corsConfigurationSource.getCorsConfiguration(request)
        assertNotNull(corsConfiguration)
        assertEquals(3600L, corsConfiguration!!.maxAge)
    }

    @Test
    @DisplayName("Should create BCrypt password encoder when requested")
    fun shouldCreateBCryptPasswordEncoderWhenRequested() {
        // Given & When
        val passwordEncoder = securityConfig.passwordEncoder()

        // Then
        assertNotNull(passwordEncoder)
        assertTrue(passwordEncoder is BCryptPasswordEncoder)
    }

    @Test
    @DisplayName("Should create authentication manager when authentication configuration is provided")
    fun shouldCreateAuthenticationManagerWhenAuthenticationConfigurationIsProvided() {
        // Given
        val authenticationConfiguration = mockk<AuthenticationConfiguration>()
        val expectedAuthenticationManager = mockk<AuthenticationManager>()
        
        io.mockk.every { authenticationConfiguration.authenticationManager } returns expectedAuthenticationManager

        // When
        val authenticationManager = securityConfig.authenticationManager(authenticationConfiguration)

        // Then
        assertNotNull(authenticationManager)
        assertEquals(expectedAuthenticationManager, authenticationManager)
    }

    @Test
    @DisplayName("Should handle different CORS paths correctly")
    fun shouldHandleDifferentCorsPathsCorrectly() {
        // Given
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // When
        val apiRequest = MockHttpServletRequest()
        apiRequest.requestURI = "/api/**"
        val apiCorsConfiguration = corsConfigurationSource.getCorsConfiguration(apiRequest)
        
        val rootRequest = MockHttpServletRequest()
        rootRequest.requestURI = "/"
        val rootCorsConfiguration = corsConfigurationSource.getCorsConfiguration(rootRequest)

        // Then
        assertNotNull(apiCorsConfiguration)
        assertNotNull(rootCorsConfiguration)
        assertEquals(apiCorsConfiguration!!.allowedOrigins, rootCorsConfiguration!!.allowedOrigins)
        assertEquals(apiCorsConfiguration.allowedMethods, rootCorsConfiguration.allowedMethods)
    }

    @Test
    @DisplayName("Should maintain consistent password encoder behavior")
    fun shouldMaintainConsistentPasswordEncoderBehavior() {
        // Given & When
        val passwordEncoder1 = securityConfig.passwordEncoder()
        val passwordEncoder2 = securityConfig.passwordEncoder()

        // Then
        assertNotNull(passwordEncoder1)
        assertNotNull(passwordEncoder2)
        assertTrue(passwordEncoder1 is BCryptPasswordEncoder)
        assertTrue(passwordEncoder2 is BCryptPasswordEncoder)
    }

    @Test
    @DisplayName("Should handle CORS configuration for specific endpoints")
    fun shouldHandleCorsConfigurationForSpecificEndpoints() {
        // Given
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // When
        val authRequest = MockHttpServletRequest()
        authRequest.requestURI = "/api/auth/login"
        val authCorsConfiguration = corsConfigurationSource.getCorsConfiguration(authRequest)
        
        val swaggerRequest = MockHttpServletRequest()
        swaggerRequest.requestURI = "/api/swagger-ui/index.html"
        val swaggerCorsConfiguration = corsConfigurationSource.getCorsConfiguration(swaggerRequest)

        // Then
        assertNotNull(authCorsConfiguration)
        assertNotNull(swaggerCorsConfiguration)
        assertTrue(authCorsConfiguration!!.allowedOrigins!!.contains("http://localhost:4200"))
        assertTrue(swaggerCorsConfiguration!!.allowedOrigins!!.contains("http://localhost:8080"))
    }

    @Test
    @DisplayName("Should create security config with JWT authentication filter dependency")
    fun shouldCreateSecurityConfigWithJwtAuthenticationFilterDependency() {
        // Given
        val customJwtFilter = mockk<JwtAuthenticationFilter>()

        // When
        val customSecurityConfig = SecurityConfig(customJwtFilter)

        // Then
        assertNotNull(customSecurityConfig)
    }

    @Test
    @DisplayName("Should handle CORS preflight requests correctly")
    fun shouldHandleCorsPrefightRequestsCorrectly() {
        // Given
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // When
        val request = MockHttpServletRequest()
        request.requestURI = "/**"
        val corsConfiguration = corsConfigurationSource.getCorsConfiguration(request)

        // Then
        assertNotNull(corsConfiguration)
        assertTrue(corsConfiguration!!.allowedMethods!!.contains("OPTIONS"))
        assertEquals(3600L, corsConfiguration.maxAge)
    }

    @Test
    @DisplayName("Should configure CORS for all application paths")
    fun shouldConfigureCorsForAllApplicationPaths() {
        // Given
        val corsConfigurationSource = securityConfig.corsConfigurationSource()

        // When
        val wildcardRequest = MockHttpServletRequest()
        wildcardRequest.requestURI = "/**"
        val wildcardCorsConfiguration = corsConfigurationSource.getCorsConfiguration(wildcardRequest)
        
        val specificRequest = MockHttpServletRequest()
        specificRequest.requestURI = "/api/users/profile"
        val specificPathCorsConfiguration = corsConfigurationSource.getCorsConfiguration(specificRequest)

        // Then
        assertNotNull(wildcardCorsConfiguration)
        assertNotNull(specificPathCorsConfiguration)
        assertEquals(wildcardCorsConfiguration!!.allowedOrigins, specificPathCorsConfiguration!!.allowedOrigins)
        assertEquals(wildcardCorsConfiguration.allowedMethods, specificPathCorsConfiguration.allowedMethods)
        assertEquals(wildcardCorsConfiguration.allowCredentials, specificPathCorsConfiguration.allowCredentials)
    }
}
