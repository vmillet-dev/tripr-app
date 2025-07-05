package com.adsearch.integration

import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.PasswordResetDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.PasswordResetRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.RegisterRequestDto
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID


/**
 * Integration tests for the Auth Controller
 * Tests authentication flows including login, registration, refresh token, and logout.
 *
 * These tests verify:
 * - UserEntity login with valid and invalid credentials
 * - UserEntity registration with new and existing usernames
 * - Token refresh functionality
 * - Logout functionality
 * - Error handling for authentication failures
 * - Response structure validation for authentication endpoints
 */
@DisplayName("Auth Controller Integration Tests")
class AuthControllerIT : BaseIT() {

    private val testUsername = "testuser"
    private val testPassword = "password"

    @Nested
    @DisplayName("Login Tests")
    inner class LoginTests {

        @Test
        @DisplayName("Should login with valid credentials")
        fun shouldLoginWithValidCredentials() {
            // Given
            val request = AuthRequestDto(testUsername, testPassword)

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/login",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull().isNotEmpty()
            assertThat(response.body).extracting("accessToken").isNotNull()
            assertThat(response.body).extracting("username").isEqualTo(testUsername)
            assertThat(response.body).extracting("roleEntities").asInstanceOf(InstanceOfAssertFactories.LIST)
                .contains("ROLE_USER")
        }

        @Test
        @DisplayName("Should return 401 with invalid credentials")
        fun shouldReturnUnauthorizedWithInvalidCredentials() {
            // Given
            val request = AuthRequestDto(testUsername, "wrongpassword")

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/login",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
            assertThat(response.body)
                .isNotNull()
                .isNotEmpty()
                .extracting("status", "error", "message", "path")
                .containsExactly(
                    401,
                    "FUNC_001",
                    "Authentication failed for user testuser - invalid credentials provided",
                    "/api/auth/login"
                )
        }

        @Test
        @DisplayName("Should return 401 with non-existent user")
        fun shouldReturnUnauthorizedWithNonExistentUser() {
            // Given
            val request = AuthRequestDto("nonexistentuser", testPassword)

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/login",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
            assertThat(response.body)
                .isNotNull()
                .isNotEmpty()
                .extracting("status", "error", "message", "path")
                .containsExactly(
                    401,
                    "FUNC_001",
                    "Authentication failed for user nonexistentuser - invalid credentials provided",
                    "/api/auth/login"
                )
        }
    }

    @Nested
    @DisplayName("Registration Tests")
    inner class RegistrationTests {

        @Test
        @DisplayName("Should register a new user")
        fun shouldRegisterNewUser() {
            // Given
            val request = RegisterRequestDto("newuser", "password123", "newuser@example.com")

            // When
            val registerResponse: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/register",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Verify user can login
            val loginRequest = AuthRequestDto("newuser", "password123")
            val loginResponse = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/login",
                httpUtil.buildJsonPayload(loginRequest),
                Map::class.java
            )

            // Then
            assertThat(registerResponse.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(registerResponse.body).isNotNull().extracting("message")
                .isEqualTo("UserEntity registered successfully")

            assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(loginResponse.body).isNotNull().extracting("accessToken").isNotNull()
            assertThat(loginResponse.body).extracting("username").isEqualTo("newuser")
        }

        @Test
        @DisplayName("Should return 400 when registering with existing username")
        fun shouldReturnBadRequestWhenRegisteringWithExistingUsername() {
            // Given
            // Using existing username
            val request = RegisterRequestDto(testUsername, "password123", "another@example.com")

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/register",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body)
                .isNotNull()
                .isNotEmpty()
                .extracting("status", "error", "message", "path")
                .containsExactly(
                    400,
                    "FUNC_004",
                    "Registration failed - username testuser already exists",
                    "/api/auth/register"
                )
        }

        @Test
        @DisplayName("Should return 400 when registering with existing email")
        fun shouldReturnBadRequestWhenRegisteringWithExistingEmail() {
            // Given
            // Using existing email
            val request = RegisterRequestDto("newuser_2", "password123", "testuser@mail.com")

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/register",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(response.body)
                .isNotNull()
                .isNotEmpty()
                .extracting("status", "error", "message", "path")
                .containsExactly(
                    400,
                    "FUNC_006",
                    "Registration failed - email testuser@mail.com already exists",
                    "/api/auth/register"
                )
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    inner class RefreshTokenTests {

        @Test
        @DisplayName("Should refresh access token with valid refresh token")
        fun shouldRefreshAccessTokenWithValidRefreshToken() {
            // Given
            val token = "e42f1f3e-ea56-47be-bc14-3b9bf1e5a58d"

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/refresh",
                httpUtil.buildJsonPayload("", token),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull().isNotEmpty()
            assertThat(response.body).extracting("accessToken").isNotNull()
            assertThat(response.body).extracting("username").isEqualTo("john_doe")
            assertThat(response.body).extracting("roleEntities").asInstanceOf(InstanceOfAssertFactories.LIST)
                .contains("ROLE_USER")
        }

        @Test
        @DisplayName("Should return 401 with invalid refresh token")
        fun shouldReturnUnauthorizedWithInvalidRefreshToken() {
            // Given
            val refreshHeaders = HttpHeaders()
            refreshHeaders.add("Cookie", "refresh-token=invalid-token; Path=/; HttpOnly")

            // When
            val refreshEntity = HttpEntity<String>(null, refreshHeaders)
            val refreshResponse: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/refresh",
                refreshEntity,
                Map::class.java
            )

            // Then
            assertThat(refreshResponse.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    inner class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        fun shouldLogoutSuccessfully() {
            // Given
            val token = "0cb1f58c-ecf9-4501-bfcb-68c527139f4e"

            // When
            val logoutResponse: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/logout",
                httpUtil.buildJsonPayload("", token),
                Map::class.java
            )

            // Then
            assertThat(logoutResponse.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(logoutResponse.body).isNotNull()
            assertThat(logoutResponse.body).extracting("message").isEqualTo("Logged out successfully")

            val refreshResponse: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/refresh",
                httpUtil.buildJsonPayload("", token),
                Map::class.java
            )

            assertThat(refreshResponse.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        }
    }

    @Nested
    @DisplayName("Password Reset Request Tests")
    inner class PasswordResetRequestTests {

        @Test
        @DisplayName("Should request password reset for existing user")
        fun shouldRequestPasswordResetForExistingUser() {
            // Given
            val request = PasswordResetRequestDto(
                username = testUsername
            )

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/password/reset-request",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )


            // Then
            val email = mailpitUtil.fetchLatestMail()
            assertThat(email)
                .isNotNull()
                .extracting("subject")
                .isEqualTo("Password Reset Request")
            assertThat(email)
                .extracting("text")
                .asString()
                .contains("http://localhost:8080/password-reset?token=")
            assertThat(email)
                .extracting("from")
                .isNotNull()
                .extracting("address")
                .isEqualTo("no-reply@example.com")
            assertThat(email)
                .extracting("to")
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .extracting("address")
                .containsExactly("testuser@mail.com")
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            assertThat(response.body)
                .extracting("message")
                .isEqualTo("If the username exists, a password reset email has been sent")
        }

        @Test
        @DisplayName("Should return OK for non-existent user (security by obscurity)")
        fun shouldReturnOkForNonExistentUser() {
            // Given
            val request = PasswordResetRequestDto("nonexistentuser")

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/password/reset-request",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response).withFailMessage("Response should not be null").isNotNull()
        }
    }

    @Nested
    @DisplayName("Password Reset Tests")
    inner class PasswordResetTests {

        @Test
        @DisplayName("Should reset password with valid token")
        fun shouldResetPasswordWithValidToken() {
            // Given
            val request = PasswordResetDto("2503c57c-a5df-43f5-823d-756a223f725f", "newpassword")

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/password/reset",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            assertThat(response.body).extracting("message").isEqualTo("Password has been reset successfully")

            // Verify login works with new password
            val loginRequest = AuthRequestDto(
                username = "Bob",
                password = "newpassword"
            )

            val loginResponse = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/login",
                httpUtil.buildJsonPayload(loginRequest),
                Map::class.java
            )

            assertThat(loginResponse.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(loginResponse.body).extracting("accessToken").isNotNull()
        }

        @Test
        @DisplayName("Should return error with invalid token")
        fun shouldReturnErrorWithInvalidToken() {
            // Given
            val request = PasswordResetDto(
                token = UUID.randomUUID().toString(),
                newPassword = "newpassword"
            )

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
                "http://localhost:$port/api/auth/password/reset",
                httpUtil.buildJsonPayload(request),
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
            assertThat(response.body)
                .isNotNull()
                .isNotEmpty()
                .extracting("status", "error", "message", "path")
                .containsExactly(
                    401,
                    "FUNC_003",
                    "Password reset failed - invalid token provided",
                    "/api/auth/password/reset"
                )
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    inner class TokenValidationTests {

        @Test
        @DisplayName("Should validate valid token")
        fun shouldValidateValidToken() {
            // Given
            val token = "ae03e5ea-0eb0-41d1-911c-491092da4798"

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.getForEntity(
                "http://localhost:$port/api/auth/password/validate-token?token=$token",
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            assertThat(response.body).extracting("valid").isEqualTo(true)
        }

        @Test
        @DisplayName("Should return false for invalid token")
        fun shouldReturnFalseForInvalidToken() {
            // Given
            val invalidToken = UUID.randomUUID().toString()

            // When
            val response: ResponseEntity<Map<*, *>> = restTemplate.getForEntity(
                "http://localhost:$port/api/auth/password/validate-token?token=$invalidToken",
                Map::class.java
            )

            // Then
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body).isNotNull()
            assertThat(response.body).extracting("valid").isEqualTo(false)
        }
    }
}
