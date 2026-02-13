package com.adsearch.integration

import com.adsearch.infrastructure.adapter.`in`.rest.dto.AuthRequestDto
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Profile
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.function.Consumer

class AuthorizationAccessIT : BaseIT() {

    @Test
    fun testPublicEndpoint() {
        restTemplate
            .get()
            .uri("/api/auth/test/public")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("value").isEqualTo("Public content")
    }

    @Test
    fun testRoleEndpoint_withAnonymousAccess() {
        obtainAccessToken("testuser")

        restTemplate
            .get()
            .uri("/api/auth/test/user")
            .exchange()
            .expectStatus().isForbidden
    }


    @Test
    fun testUserEndpoint_WithUserRole() {
        restTemplate
            .get()
            .uri("/api/auth/test/user")
            .headers(obtainAccessToken("testuser"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("value").isEqualTo("UserEntity content")
    }

    @Test
    fun testAdminEndpoint_WithUserRole() {
        restTemplate
            .get()
            .uri("/api/auth/test/admin")
            .headers(obtainAccessToken("testuser"))
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun testUserEndpoint_WithAdminRole() {
        restTemplate
            .get()
            .uri("/api/auth/test/admin")
            .headers(obtainAccessToken("testadmin"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("value").isEqualTo("Admin content")
    }

    private fun obtainAccessToken(username: String): Consumer<HttpHeaders> {
        // Given
        val request = AuthRequestDto(username, "password")

        // When
        val response = restTemplate
            .post()
            .uri("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .exchange()
            .expectBody(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .returnResult()
            .responseBody

        return Consumer { headers: HttpHeaders? ->
            headers!!.set("Authorization", "Bearer ${response!!["accessToken"] as String}")
        }
    }
}

data class MyResponse(val value: String)

@RestController
@RequestMapping("/auth/test")
@Profile("test")
class TestController {
    @GetMapping("/public")
    fun publicAccess(): ResponseEntity<MyResponse> {
        return ResponseEntity.ok(MyResponse("Public content"))
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    fun userAccess(): ResponseEntity<MyResponse> {
        return ResponseEntity.ok(MyResponse("UserEntity content"))
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminAccess(): ResponseEntity<MyResponse> {
        return ResponseEntity.ok(MyResponse("Admin content"))
    }
}
