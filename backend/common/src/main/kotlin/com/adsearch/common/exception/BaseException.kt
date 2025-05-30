package com.adsearch.common.exception

import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.enum.LogLevelEnum

abstract class BaseException(
    override val message: String,
    val errorCode: String,
    val httpStatusEnum: HttpStatusEnum,
    cause: Throwable? = null,
    val logLevelEnum: LogLevelEnum,
) : Exception(message, cause)
