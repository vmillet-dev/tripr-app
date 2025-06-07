package com.adsearch.infrastructure.security

import com.adsearch.infrastructure.config.CustomMetricsConfig
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
@Order(1)
class RequestLoggingFilter(
    private val customMetrics: CustomMetricsConfig
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val correlationId = UUID.randomUUID().toString()
        MDC.put("correlationId", correlationId)
        
        val startTime = System.currentTimeMillis()
        val sample = customMetrics.recordApiCall(
            request.requestURI,
            request.method,
            "unknown"
        )

        try {
            logger.info(
                "Incoming request: {} {} from IP: {} User-Agent: {}",
                request.method,
                request.requestURI,
                getClientIpAddress(request),
                request.getHeader("User-Agent")
            )

            filterChain.doFilter(request, response)

            val duration = System.currentTimeMillis() - startTime
            val status = response.status.toString()

            customMetrics.recordApiCallEnd(sample, request.requestURI, request.method, status)

            logger.info(
                "Request completed: {} {} - Status: {} Duration: {}ms",
                request.method,
                request.requestURI,
                status,
                duration
            )

        } catch (e: Exception) {
            customMetrics.recordApiCallEnd(sample, request.requestURI, request.method, "error")
            logger.error("Request failed: {} {} - Error: {}", request.method, request.requestURI, e.message)
            throw e
        } finally {
            MDC.clear()
        }
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrBlank()) {
            return xForwardedFor.split(",")[0].trim()
        }

        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrBlank()) {
            return xRealIp
        }

        return request.remoteAddr
    }
}
