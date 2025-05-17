package com.adsearch.common.exception

import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.enum.LogLevelEnum

abstract class BaseFunctionalException(
    message: String,
    errorCode: String,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.BAD_REQUEST,
    cause: Throwable? = null
) : BaseException(message, errorCode, httpStatusEnum, cause, LogLevelEnum.WARN)
