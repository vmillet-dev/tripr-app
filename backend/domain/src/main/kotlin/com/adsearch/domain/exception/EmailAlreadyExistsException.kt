package com.adsearch.domain.exception

import com.adsearch.domain.enum.FunctionalErrorCodeEnum
import com.adsearch.domain.enum.HttpStatusEnum

class EmailAlreadyExistsException(
    message: String = FunctionalErrorCodeEnum.EMAIL_ALREADY_EXISTS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.EMAIL_ALREADY_EXISTS.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.BAD_REQUEST,
    cause: Throwable? = null
) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)
