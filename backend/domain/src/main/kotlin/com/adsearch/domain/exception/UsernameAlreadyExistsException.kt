package com.adsearch.domain.exception

import com.adsearch.domain.model.enum.FunctionalErrorCodeEnum
import com.adsearch.domain.model.enum.HttpStatusEnum

class UsernameAlreadyExistsException(
    message: String = FunctionalErrorCodeEnum.USERNAME_ALREADY_EXISTS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.USERNAME_ALREADY_EXISTS.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.BAD_REQUEST,
    cause: Throwable? = null
) : BaseFunctionalException(message, cause, errorCode, httpStatusEnum)
