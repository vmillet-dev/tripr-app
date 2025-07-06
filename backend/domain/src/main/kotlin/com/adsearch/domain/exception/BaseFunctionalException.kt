package com.adsearch.domain.exception

import com.adsearch.domain.enum.HttpStatusEnum

abstract class BaseFunctionalException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: String,
    val httpStatusEnum: HttpStatusEnum = HttpStatusEnum.BAD_REQUEST,
) : Exception(message, cause)
