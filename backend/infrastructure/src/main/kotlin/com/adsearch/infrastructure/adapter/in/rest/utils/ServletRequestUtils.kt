package com.adsearch.infrastructure.adapter.`in`.rest.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object ServletRequestUtils {

    fun currentRequest(): HttpServletRequest =
        (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
            ?: error("No HTTP request found in current thread context")

    fun currentResponse(): HttpServletResponse =
        (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.response
            ?: error("No HTTP response found in current thread context")
}
