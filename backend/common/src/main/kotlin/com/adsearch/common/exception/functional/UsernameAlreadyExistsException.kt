package com.adsearch.common.exception.functional

import com.adsearch.common.enum.FunctionalErrorCodeEnum
import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.exception.BaseFunctionalException

class UsernameAlreadyExistsException(
    message: String = FunctionalErrorCodeEnum.USERNAME_ALREADY_EXISTS.defaultMessage,
    errorCode: String = FunctionalErrorCodeEnum.USERNAME_ALREADY_EXISTS.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.BAD_REQUEST,
    cause: Throwable? = null
) : BaseFunctionalException(message, errorCode, httpStatusEnum, cause)
