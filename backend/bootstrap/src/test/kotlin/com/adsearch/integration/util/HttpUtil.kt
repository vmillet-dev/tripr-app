package com.adsearch.integration.util

import org.springframework.context.annotation.Profile
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Utility class for HTTP-related operations in tests
 */
@Component
@Profile("test")
class HttpUtil {

    /**
     * Builds an HTTP entity with JSON content type and optional authentication token.
     *
     * @param payload The object to be serialized as JSON in the request body
     * @param token Optional refresh token to include in cookies
     * @param additionalHeaders Optional map of additional headers to include
     * @return An HttpEntity configured with the appropriate headers and body
     */
    fun buildJsonPayload(
        payload: Any,
        token: String? = null,
        additionalHeaders: Map<String, String> = emptyMap()
    ): HttpEntity<*> {
        return HttpHeaders().apply {
            // Set content type to JSON
            contentType = MediaType.APPLICATION_JSON

            // Add refresh token cookie if provided
            token?.let {
                add(HttpHeaders.COOKIE, buildRefreshTokenCookie(it))
            }

            // Add any additional headers
            additionalHeaders.forEach { (name, value) ->
                add(name, value)
            }
        }.let { headers ->
            HttpEntity(payload, headers)
        }
    }

    /**
     * Creates a properly formatted refresh token cookie string.
     *
     * @param token The refresh token value
     * @param maxAgeDays The number of days until the cookie expires (default 7)
     * @return Formatted cookie string
     */
    fun buildRefreshTokenCookie(token: String, maxAgeDays: Long = 7): String {
        val maxAge = TimeUnit.DAYS.toSeconds(maxAgeDays)
        val expires = ZonedDateTime.now().plusDays(maxAgeDays).format(DateTimeFormatter.RFC_1123_DATE_TIME)

        return "refresh-token=$token; Max-Age=$maxAge; Expires=$expires; Path=/; HttpOnly; SameSite=Strict"
    }
}
