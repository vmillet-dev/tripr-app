package com.adsearch.domain.exception

import com.adsearch.domain.exception.enums.HttpStatusEnum
import com.adsearch.domain.exception.enums.TechnicalErrorCodeEnum

class MailSendException(
    message: String = TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage,
    errorCode: String = TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : BaseTechnicalException(message, cause, errorCode, httpStatusEnum)
