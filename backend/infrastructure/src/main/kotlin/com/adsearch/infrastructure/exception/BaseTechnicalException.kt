package com.adsearch.infrastructure.exception

import com.adsearch.domain.model.enums.HttpStatusEnum

abstract class BaseTechnicalException(
    override val message: String,
    override val cause: Throwable? = null,
    val errorCode: String,
    val httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR,
) : Exception(message, cause)
