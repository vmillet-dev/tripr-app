package com.adsearch.domain.exception

import com.adsearch.domain.exception.enums.HttpStatusEnum

abstract class BaseTechnicalException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: String,
    val httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR,
) : Exception(message, cause)
