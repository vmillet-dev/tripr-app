package com.adsearch.common.exception.functional

import com.adsearch.common.enum.FunctionalErrorCodeEnum
import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.exception.BaseFunctionalException

class UserNotFoundException(
    message: String = FunctionalErrorCodeEnum.USER_NOT_FOUND.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.USER_NOT_FOUND.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.UNAUTHORIZED,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatusEnum, cause)
