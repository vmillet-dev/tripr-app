package com.adsearch.domain.exception

import org.springframework.http.HttpStatus

abstract class BaseFunctionalException(
    message: String,
    errorCode: String,
    httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    cause: Throwable? = null
) : BaseException(message, errorCode, httpStatus, cause)
