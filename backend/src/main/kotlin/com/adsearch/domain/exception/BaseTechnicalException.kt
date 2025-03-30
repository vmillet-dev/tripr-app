package com.adsearch.domain.exception

import org.springframework.http.HttpStatus

abstract class BaseTechnicalException(
    message: String,
    errorCode: String,
    httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : BaseException(message, errorCode, httpStatus, cause)
