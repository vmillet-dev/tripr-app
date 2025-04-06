package com.adsearch.common.exception

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

abstract class BaseException(
    override val message: String,
    val errorCode: String,
    val httpStatus: HttpStatus,
    cause: Throwable? = null,
    val logLevel: LogLevel = LogLevel.WARN,
) : Exception(message, cause)
