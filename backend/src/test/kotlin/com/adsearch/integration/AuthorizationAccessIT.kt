package com.adsearch.integration

import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthRequestDto
import org.assertj.core.api.Assertions.assertThat
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.test.Test

class AuthorizationAccessIT : BaseIT() {

    @Test
    fun testPublicEndpoint() {
        val response: ResponseEntity<String?> = restTemplate.getForEntity(
            "http://localhost:$port/api/auth/test/public", String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo("Public content")
    }

    @Test
    fun testRoleEndpoint_withAnonymousAccess() {
        val response: ResponseEntity<String?> = restTemplate.getForEntity(
            "http://localhost:$port/api/auth/test/user", String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }


    @Test
    fun testUserEndpoint_WithUserRole() {
        val response: ResponseEntity<String?>? = restTemplate.exchange(
            "http://localhost:$port/api/auth/test/user",
            HttpMethod.GET,
            obtainAccessToken("testuser"),
            String::class.java
        )

        assertThat(response!!.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo("User content")
    }

    @Test
    fun testAdminEndpoint_WithUserRole() {
        val response: ResponseEntity<String?>? = restTemplate.exchange(
            "http://localhost:$port/api/auth/test/admin",
            HttpMethod.GET,
            obtainAccessToken("testuser"),
            String::class.java
        )

        assertThat(response!!.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun testUserEndpoint_WithAdminRole() {
        val response: ResponseEntity<String?>? = restTemplate.exchange(
            "http://localhost:$port/api/auth/test/admin",
            HttpMethod.GET,
            obtainAccessToken("testadmin"),
            String::class.java
        )

        assertThat(response!!.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo("Admin content")
    }

    private fun obtainAccessToken(username: String): HttpEntity<Void> {
        // Given
        val request = AuthRequestDto(username, "password")

        // When
        val response: ResponseEntity<Map<*, *>> = restTemplate.postForEntity(
            "http://localhost:$port/api/auth/login",
            httpUtil.buildJsonPayload(request),
            Map::class.java
        )

        val authHeaders = HttpHeaders()
        authHeaders.set("Authorization", "Bearer ${response.body!!["accessToken"] as String}")

        return HttpEntity<Void>(authHeaders)
    }


}

@RestController
@RequestMapping("/auth/test")
@Profile("test")
class TestController {
    @GetMapping("/public")
    fun publicAccess(): ResponseEntity<String?> {
        return ResponseEntity.ok<String?>("Public content")
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    fun userAccess(): ResponseEntity<String?> {
        return ResponseEntity.ok<String?>("User content")
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminAccess(): ResponseEntity<String?> {
        return ResponseEntity.ok<String?>("Admin content")
    }
}
