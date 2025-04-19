package com.adsearch.common.exception

import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.enum.LogLevelEnum

abstract class BaseTechnicalException(
    message: String,
    errorCode: String,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : BaseException(message, errorCode, httpStatusEnum, cause, LogLevelEnum.ERROR)
